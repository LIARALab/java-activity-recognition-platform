package org.liara.api.filter.visitor.collection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
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

public class EntityCollectionDateTimeInRangeFilterVisitor<Entity> 
       extends AnnotationBasedFilterASTVisitor 
       implements EntityCollectionFilterVisitor<Entity, LocalDateTime>
{
  @FunctionalInterface
  private interface PredicateFactory {
    public <Value extends Comparable<? super Value>> Predicate create (@NonNull final Expression<? extends Value> a, @NonNull final Value b);
  }
  
  @NonNull
  private final List<Predicate> _stack = new ArrayList<>();
  
  @Nullable
  private EntityCollectionQuery<Entity> _query = null;
  
  @NonNull
  private final EntityFieldSelector<Entity, Expression<LocalDateTime>> _start;
  
  @NonNull
  private final EntityFieldSelector<Entity, Expression<LocalDateTime>> _end;
  
  public EntityCollectionDateTimeInRangeFilterVisitor(
    @NonNull final EntityFieldSelector<Entity, Expression<LocalDateTime>> start,
    @NonNull final EntityFieldSelector<Entity, Expression<LocalDateTime>> end
  )
  {
    _start = start;
    _end = end;
  }

  @Override
  public Predicate filter (
    @NonNull final EntityCollectionQuery<Entity> query, 
    @NonNull final PredicateFilterNode predicate
  ) {
    _query = query;
    visit(predicate);
    _query = null;
    return _stack.remove(0);
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.CONJUNCTION)
  public void visit (@NonNull final ConjunctionFilterNode conjunction) {
    final List<Predicate> predicates = this.visitComposite(conjunction);
    _stack.add(
      _query.getManager().getCriteriaBuilder().and(
        predicates.toArray(new Predicate[predicates.size()])
      )
    );
  }
  
  @VisitCommonFilterNode(type = CommonFilterNodeType.DISJUNCTION)
  public void visit (@NonNull final DisjunctionFilterNode disjunction) {
    final List<Predicate> predicates = this.visitComposite(disjunction);
    _stack.add(
      _query.getManager().getCriteriaBuilder().or(
        predicates.toArray(new Predicate[predicates.size()])
      )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.GREATHER_THAN)
  public void visit (@NonNull final GreaterThanFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(handleDate(_end, _query.getManager().getCriteriaBuilder()::greaterThan, predicate.getMinimum()));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.GREATHER_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final GreaterThanOrEqualToFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(handleDate(
      _end, 
      _query.getManager().getCriteriaBuilder()::greaterThanOrEqualTo, 
      predicate.getMinimum()
    ));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN)
  public void visit (@NonNull final LessThanFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(handleDate(
      _start, 
      _query.getManager().getCriteriaBuilder()::lessThan, 
      predicate.getMaximum()
    ));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final LessThanOrEqualToFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(handleDate(
      _start, 
      _query.getManager().getCriteriaBuilder()::lessThanOrEqualTo, 
      predicate.getMaximum()
    ));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.EQUAL_TO)
  public void visit (@NonNull final EqualToFilterNode<PartialLocalDateTime> predicate) {
    _stack.add(_query.getManager().getCriteriaBuilder().and(
      handleDate(
        _end, 
        _query.getManager().getCriteriaBuilder()::greaterThanOrEqualTo,
        predicate.getValue()
      ),
      handleDate(
        _start, 
        _query.getManager().getCriteriaBuilder()::lessThanOrEqualTo,
        predicate.getValue()
      )
     ));
  }
  
  private Predicate handleDate (
    @NonNull final EntityFieldSelector<Entity, Expression<LocalDateTime>> dateField,
    @NonNull final PredicateFactory operator,
    @NonNull final PartialLocalDateTime date
  ) {
    if (date.isCompleteLocalDateTime()) {
      return operator.create(dateField.select(_query), date.toLocalDateTime());
    } else {
      final List<Predicate> predicates = new ArrayList<>();
      
      if (date.containsDatetime()) {
        predicates.add(
          operator.create(
            date.mask(dateField.select(_query), _query.getManager().getCriteriaBuilder()),
            date.toLocalDateTime()
          )
        );
      }
      
      if (date.containsContext()) {
        Arrays.stream(PartialLocalDateTime.CONTEXT_FIELDS)
              .filter(date::isSupported)
              .map(
                field -> operator.create(
                   PartialLocalDateTime.select(
                     dateField.select(_query), 
                     _query.getManager().getCriteriaBuilder(), 
                     field
                   ), date.getLong(field)
                 )
               )
              .forEach(predicates::add);
      }
      
      return _query.getManager().getCriteriaBuilder().and(predicates.toArray(new Predicate[predicates.size()])); 
    }
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.BETWEEN)
  public void visit (@NonNull final BetweenFilterNode<PartialLocalDateTime> predicate) {
    final PartialLocalDateTime minimum = predicate.getMinimum();
    final PartialLocalDateTime maximum = predicate.getMaximum();
    
    _stack.add(
      _query.getManager().getCriteriaBuilder().and(
        handleDate(_end, _query.getManager().getCriteriaBuilder()::greaterThanOrEqualTo, minimum),
        handleDate(_start, _query.getManager().getCriteriaBuilder()::lessThanOrEqualTo, maximum)
      )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.NOT)
  public void visit (@NonNull final NotFilterNode predicate) {
    predicate.getChild(0).invit(this);
    _stack.set(
      _stack.size() - 1, 
      _query.getManager().getCriteriaBuilder().not(
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
