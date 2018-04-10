package org.liara.api.filter.visitor.criteria;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.filter.ast.CommonFilterNodeType;
import org.liara.api.filter.ast.CompositeFilterNode;
import org.liara.api.filter.ast.ConjunctionFilterNode;
import org.liara.api.filter.ast.DisjunctionFilterNode;
import org.liara.api.filter.ast.FilterNode;
import org.liara.api.filter.ast.LikeFilterNode;
import org.liara.api.filter.ast.NotFilterNode;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.liara.api.filter.ast.RegexpFilterNode;
import org.liara.api.filter.visitor.AnnotationBasedFilterASTVisitor;
import org.liara.api.filter.visitor.VisitCommonFilterNode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class CriteriaTextFilterASTVisitor<Entity> extends AnnotationBasedFilterASTVisitor implements CriteriaFilterASTVisitor<Entity, String>
{  
  @NonNull
  private final List<Predicate> _stack = new ArrayList<>();
  
  @Nullable
  private CriteriaFilterASTVisitorContext<Entity> _context = null;

  @NonNull
  private final CriteriaExpressionSelector<String> _field;

  public CriteriaTextFilterASTVisitor (@NonNull final CriteriaExpressionSelector<String> field) {
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
  
  @VisitCommonFilterNode(type = CommonFilterNodeType.LIKE)
  public void visit (@NonNull final LikeFilterNode predicate) {
    _stack.add(
      _context.getCriteriaBuilder().like(_context.select(_field), predicate.getValue())
    );
  }
  
  @VisitCommonFilterNode(type = CommonFilterNodeType.REGEXP)
  public void visit (@NonNull final RegexpFilterNode predicate) {
    _stack.add(
      _context.getCriteriaBuilder().equal(
        _context.getCriteriaBuilder().function(
          "regexp", 
          Integer.class, 
          _context.select(_field), 
          _context.getCriteriaBuilder().literal(predicate.getValue())
        ),
        1
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
