package org.liara.api.filter.visitor.criteria;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
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

public class CriteriaComparableFilterASTVisitor<Entity, Value extends Comparable<? super Value>> extends AnnotationBasedFilterASTVisitor implements CriteriaFilterASTVisitor<Entity, Value>
{
  @NonNull
  private final List<Predicate> _stack = new ArrayList<>();
  
  @Nullable
  private CriteriaFilterASTVisitorContext<Entity> _context = null;

  @NonNull
  private final SimplifiedCriteriaExpressionSelector<Value> _field;
  
  public CriteriaComparableFilterASTVisitor (@NonNull final SimplifiedCriteriaExpressionSelector<Value> field) {
    _field = field;
  }
  
  @Override
  public Predicate visit (
    @NonNull final CriteriaFilterASTVisitorContext<Entity> context, 
    @NonNull final PredicateFilterNode predicate
  ) {
    _context = context;
    visit(predicate);
    _context = null;
    return _stack.remove(0);
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
  public void visit (@NonNull final GreaterThanFilterNode<Value> predicate) {
    _stack.add(
      _context.getCriteriaBuilder().greaterThan(
        _context.select(_field),
        predicate.getMinimum()
       )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.GREATHER_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final GreaterThanOrEqualToFilterNode<Value> predicate) {
    _stack.add(
      _context.getCriteriaBuilder().greaterThanOrEqualTo(
        _context.select(_field), 
        predicate.getMinimum()
      )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN)
  public void visit (@NonNull final LessThanFilterNode<Value> predicate) {
    _stack.add(
      _context.getCriteriaBuilder().lessThan(
        _context.select(_field), predicate.getMaximum()
      )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final LessThanOrEqualToFilterNode<Value> predicate) {
    _stack.add(
      _context.getCriteriaBuilder().lessThanOrEqualTo(
        _context.select(_field), predicate.getMaximum()
      )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.EQUAL_TO)
  public void visit (@NonNull final EqualToFilterNode<Value> predicate) {
    _stack.add(
      _context.getCriteriaBuilder().equal(
        _context.select(_field), 
        predicate.getValue()
      )
    );
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.BETWEEN)
  public void visit (@NonNull final BetweenFilterNode<Value> predicate) {
    _stack.add(
      _context.getCriteriaBuilder().between(
        _context.select(_field), 
        predicate.getMinimum(), 
        predicate.getMaximum()
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
