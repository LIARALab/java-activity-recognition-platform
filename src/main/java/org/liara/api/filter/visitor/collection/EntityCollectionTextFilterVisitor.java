/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.filter.visitor.collection;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
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

public class EntityCollectionTextFilterVisitor<Entity> extends AnnotationBasedFilterASTVisitor implements EntityCollectionFilterVisitor<Entity, String>
{  
  @NonNull
  private final List<Predicate> _stack = new ArrayList<>();
  
  @Nullable
  private EntityCollectionQuery<Entity, ?> _query = null;

  @NonNull
  private final EntityFieldSelector<Entity, Expression<String>> _field;

  public EntityCollectionTextFilterVisitor (@NonNull final EntityFieldSelector<Entity, Expression<String>> field) {
    _field = field;
  }
  
  @Override
  public Predicate filter (
    @NonNull final EntityCollectionQuery<Entity, ?> context, 
    @NonNull final PredicateFilterNode predicate
  ) {
    _query = context;
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
  
  @VisitCommonFilterNode(type = CommonFilterNodeType.LIKE)
  public void visit (@NonNull final LikeFilterNode predicate) {
    _stack.add(
      _query.getManager().getCriteriaBuilder().like(
        _field.select(_query), predicate.getValue()
      )
    );
  }
  
  @VisitCommonFilterNode(type = CommonFilterNodeType.REGEXP)
  public void visit (@NonNull final RegexpFilterNode predicate) {
    final CriteriaBuilder builder = _query.getManager().getCriteriaBuilder();
    _stack.add(
      builder.equal(
        builder.function(
          "regexp", 
          Integer.class, 
          _field.select(_query), 
          builder.literal(predicate.getValue())
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