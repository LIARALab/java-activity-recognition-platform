package org.liara.api.filter.visitor.criteria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.date.PartialLocalDateTime;
import org.liara.api.filter.ast.BetweenFilterNode;
import org.liara.api.filter.ast.CommonFilterNodeType;
import org.liara.api.filter.ast.CompositeFilterNode;
import org.liara.api.filter.ast.ConjunctionFilterNode;
import org.liara.api.filter.ast.DisjunctionFilterNode;
import org.liara.api.filter.ast.EqualToFilterNode;
import org.liara.api.filter.ast.FilterNode;
import org.liara.api.filter.ast.GreaterThanFilterNode;
import org.liara.api.filter.ast.GreaterThanOrEqualToFilterNode;
import org.liara.api.filter.ast.LessThanFilterNode;
import org.liara.api.filter.ast.LessThanOrEqualToFilterNode;
import org.liara.api.filter.ast.NotFilterNode;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.liara.api.filter.visitor.AnnotationBasedFilterASTVisitor;
import org.liara.api.filter.visitor.VisitCommonFilterNode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CriteriaDateTimeInRangeFilterASTVisitor<Entity> extends AnnotationBasedFilterASTVisitor implements CriteriaFilterASTVisitor<Entity, LocalDateTime>
{
  @FunctionalInterface
  private interface PredicateFactory {
    public <Value extends Comparable<? super Value>> Predicate create (@NonNull final Expression<? extends Value> a, @NonNull final Value b);
  }
  
  @NonNull
  private final List<Predicate> _stack = new ArrayList<>();
  
  @Nullable
  private CriteriaFilterASTVisitorContext<Entity> _context = null;
  
  @NonNull
  private final CriteriaExpressionSelector<LocalDateTime> _start;
  
  @NonNull
  private final CriteriaExpressionSelector<LocalDateTime> _end;
  
  public CriteriaDateTimeInRangeFilterASTVisitor(
    @NonNull final CriteriaExpressionSelector<LocalDateTime> start,
    @NonNull final CriteriaExpressionSelector<LocalDateTime> end
  )
  {
    _start = start;
    _end = end;
  }

  @Override
  public void visit (
    @NonNull final CriteriaFilterASTVisitorContext<Entity> context, 
    @NonNull final PredicateFilterNode predicate
  ) {
    _context = context;
    visit(predicate);
    context.getCriteriaQuery().where(_stack.remove(0));
    _context = null;
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.CONJUNCTION)
  public void visit (@NonNull final ConjunctionFilterNode conjunction) {
    final List<Predicate> predicates = this.visitComposite(conjunction);
    _stack.add(
      _context.getCriteriaBuilder().and(
        predicates.toArray(new Predicate[predicates.size()])
      )
    );
  }
  
  @VisitCommonFilterNode(type = CommonFilterNodeType.DISJUNCTION)
  public void visit (@NonNull final DisjunctionFilterNode disjunction) {
    final List<Predicate> predicates = this.visitComposite(disjunction);
    _stack.add(
      _context.getCriteriaBuilder().or(
        predicates.toArray(new Predicate[predicates.size()])
      )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.GREATHER_THAN)
  public void visit (@NonNull final GreaterThanFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(handleDate(_end, _context.getCriteriaBuilder()::greaterThan, predicate.getMinimum()));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.GREATHER_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final GreaterThanOrEqualToFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(handleDate(_end, _context.getCriteriaBuilder()::greaterThanOrEqualTo, predicate.getMinimum()));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN)
  public void visit (@NonNull final LessThanFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(handleDate(_start, _context.getCriteriaBuilder()::lessThan, predicate.getMaximum()));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final LessThanOrEqualToFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(handleDate(_start, _context.getCriteriaBuilder()::lessThanOrEqualTo, predicate.getMaximum()));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.EQUAL_TO)
  public void visit (@NonNull final EqualToFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(_context.getCriteriaBuilder().and(
      handleDate(_end, _context.getCriteriaBuilder()::greaterThanOrEqualTo, predicate.getValue()),
      handleDate(_start, _context.getCriteriaBuilder()::lessThanOrEqualTo, predicate.getValue())
     ));
  }
  
  private Predicate handleDate (
    @NonNull final CriteriaExpressionSelector<LocalDateTime> dateField,
    @NonNull final PredicateFactory operator,
    @NonNull final PartialLocalDateTime date
  ) {
    if (date.isCompleteLocalDateTime()) {
      return operator.create(_context.select(dateField), date.toLocalDateTime());
    } else {
      final List<Predicate> predicates = new ArrayList<>();
      
      if (date.containsDatetime()) {
        predicates.add(
          operator.create(
            date.mask(_context.select(dateField), _context.getCriteriaBuilder()),
            date.toLocalDateTime()
          )
        );
      }
      
      if (date.containsContext()) {
        Arrays.stream(PartialLocalDateTime.CONTEXT_FIELDS)
              .filter(date::isSupported)
              .map(
                field -> operator.create(
                   PartialLocalDateTime.select(_context.select(dateField), _context.getCriteriaBuilder(), field), 
                   date.getLong(field)
                 )
               )
              .forEach(predicates::add);
      }
      
      return _context.getCriteriaBuilder().and(predicates.toArray(new Predicate[predicates.size()])); 
    }
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.BETWEEN)
  public void visit (@NonNull final BetweenFilterNode<PartialLocalDateTime> predicate) {
    final PartialLocalDateTime minimum = predicate.getMinimum();
    final PartialLocalDateTime maximum = predicate.getMaximum();
    
    _stack.add(
      _context.getCriteriaBuilder().and(
        handleDate(_end, _context.getCriteriaBuilder()::greaterThanOrEqualTo, minimum),
        handleDate(_start, _context.getCriteriaBuilder()::lessThanOrEqualTo, maximum)
      )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.NOT)
  public void visit (@NonNull final NotFilterNode predicate) {
    predicate.getChild(0).invit(this);
    _stack.set(
      _stack.size() - 1, 
      _context.getCriteriaBuilder().not(
        _stack.get(_stack.size() - 1)
      )
    );
  }
  
  private List<Predicate> visitComposite (@NonNull final CompositeFilterNode<PredicateFilterNode> composite) {
    for (final PredicateFilterNode predicate : composite) {
      predicate.invit(this);
    }
    
    final List<Predicate> result = new ArrayList<>(composite.getChildCount());
    
    for (int index = 0; index < composite.getChildCount(); ++index) {
      result.add(0, _stack.remove(_stack.size() - 1));
    }
    
    return result;
  }
  
  public void visitDefault (@NonNull final FilterNode node) {
    throw new Error("Unhandled node " + node);
  }
}