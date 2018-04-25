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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.date.PartialZonedDateTime;
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
       implements EntityCollectionFilterVisitor<Entity, ZonedDateTime>
{
  @FunctionalInterface
  private interface PredicateFactory {
    public <Value extends Comparable<? super Value>> Predicate create (@NonNull final Expression<? extends Value> a, @NonNull final Value b);
  }
  
  @NonNull
  private final List<Predicate> _stack = new ArrayList<>();
  
  @Nullable
  private EntityCollectionQuery<Entity, ?> _query = null;
  
  @NonNull
  private final EntityFieldSelector<Entity, Expression<ZonedDateTime>> _start;
  
  @NonNull
  private final EntityFieldSelector<Entity, Expression<ZonedDateTime>> _end;
  
  public EntityCollectionDateTimeInRangeFilterVisitor(
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> start,
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> end
  )
  {
    _start = start;
    _end = end;
  }

  @Override
  public Predicate filter (
    @NonNull final EntityCollectionQuery<Entity, ?> query, 
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
  public void visit (@NonNull final GreaterThanFilterNode<PartialZonedDateTime> predicate) {
    _stack.add(handleDate(_end, _query.getManager().getCriteriaBuilder()::greaterThan, predicate.getMinimum()));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.GREATHER_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final GreaterThanOrEqualToFilterNode<PartialZonedDateTime> predicate) {
    _stack.add(handleDate(
      _end, 
      _query.getManager().getCriteriaBuilder()::greaterThanOrEqualTo, 
      predicate.getMinimum()
    ));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN)
  public void visit (@NonNull final LessThanFilterNode<PartialZonedDateTime> predicate) {
    _stack.add(handleDate(
      _start, 
      _query.getManager().getCriteriaBuilder()::lessThan, 
      predicate.getMaximum()
    ));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final LessThanOrEqualToFilterNode<PartialZonedDateTime> predicate) {
    _stack.add(handleDate(
      _start, 
      _query.getManager().getCriteriaBuilder()::lessThanOrEqualTo, 
      predicate.getMaximum()
    ));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.EQUAL_TO)
  public void visit (@NonNull final EqualToFilterNode<PartialZonedDateTime> predicate) {
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
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> dateField,
    @NonNull final PredicateFactory operator,
    @NonNull final PartialZonedDateTime date
  ) {
    if (date.isCompleteZonedDateTime()) {
      return operator.create(dateField.select(_query), date.toZonedDateTime());
    } else {
      final List<Predicate> predicates = new ArrayList<>();
      
      if (date.containsDatetime()) {
        predicates.add(
          operator.create(
            date.mask(dateField.select(_query), _query.getManager().getCriteriaBuilder()),
            date.toZonedDateTime()
          )
        );
      }
      
      if (date.containsContext()) {
        Arrays.stream(PartialZonedDateTime.CONTEXT_FIELDS)
              .filter(date::isSupported)
              .map(
                field -> operator.create(
                   PartialZonedDateTime.select(
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
  public void visit (@NonNull final BetweenFilterNode<PartialZonedDateTime> predicate) {
    final PartialZonedDateTime minimum = predicate.getMinimum();
    final PartialZonedDateTime maximum = predicate.getMaximum();
    
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
