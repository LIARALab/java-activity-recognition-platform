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

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.date.PartialZonedDateTime;
import org.liara.api.filter.ast.*;
import org.liara.api.filter.visitor.AnnotationBasedFilterASTVisitor;
import org.liara.api.filter.visitor.VisitCommonFilterNode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class      EntityCollectionDateTimeFilterVisitor<Entity> 
       extends    AnnotationBasedFilterASTVisitor
       implements EntityCollectionFilterVisitor<Entity, ZonedDateTime>
{
  private Predicate handleDate (
    @NonNull final PartialZonedDateTime date,
    @NonNull final PredicateFactory operator
  ) {
    if (date.isCompleteZonedDateTime()) {
      return operator.create(
        date.zone(_field.select(_query), _query.getManager().getCriteriaBuilder()),
        date.toZonedDateTime()
      );
    } else {
      final List<Predicate> predicates = new ArrayList<>();

      if (date.containsDatetime()) {
        predicates.add(
          operator.create(
            date.mask(_field.select(_query), _query.getManager().getCriteriaBuilder()),
            date.toZonedDateTime()
          )
        );
      }

      if (date.containsContext()) {
        Arrays.stream(PartialZonedDateTime.CONTEXT_FIELDS)
          .filter(date::isSupported)
          .map(
            field -> operator.create(
              date.select(_field.select(_query), _query.getManager().getCriteriaBuilder(), field),
              date.getLong(field)
            )
          )
          .forEach(predicates::add);
      }

      return _query.getManager()
               .getCriteriaBuilder()
               .and(predicates.toArray(new Predicate[predicates.size()]));
    }
  }
  
  @NonNull
  private final List<Predicate> _stack = new ArrayList<>();
  
  @Nullable
  private EntityCollectionQuery<Entity, ?> _query = null;

  @NonNull
  private final EntityFieldSelector<Entity, Expression<ZonedDateTime>> _field;

  public EntityCollectionDateTimeFilterVisitor (
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> field
  ) {
    _field = field;
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
    _stack.add(handleDate(predicate.getMinimum(), _query.getManager().getCriteriaBuilder()::greaterThan));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.GREATHER_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final GreaterThanOrEqualToFilterNode<PartialZonedDateTime> predicate) {
    _stack.add(handleDate(predicate.getMinimum(), _query.getManager().getCriteriaBuilder()::greaterThanOrEqualTo));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN)
  public void visit (@NonNull final LessThanFilterNode<PartialZonedDateTime> predicate) {
    _stack.add(handleDate(predicate.getMaximum(), _query.getManager().getCriteriaBuilder()::lessThan));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.LESS_THAN_OR_EQUAL_TO)
  public void visit (@NonNull final LessThanOrEqualToFilterNode<PartialZonedDateTime> predicate) {
    _stack.add(handleDate(predicate.getMaximum(), _query.getManager().getCriteriaBuilder()::lessThanOrEqualTo));
  }

  @VisitCommonFilterNode(type = CommonFilterNodeType.EQUAL_TO)
  public void visit (@NonNull final EqualToFilterNode<PartialZonedDateTime> predicate) {
    _stack.add(handleDate(predicate.getValue(), _query.getManager().getCriteriaBuilder()::equal));
  }
  
  @VisitCommonFilterNode(type = CommonFilterNodeType.BETWEEN)
  public void visit (@NonNull final BetweenFilterNode<PartialZonedDateTime> predicate) {
    final PartialZonedDateTime minimum = predicate.getMinimum();
    final PartialZonedDateTime maximum = predicate.getMaximum();

    if (minimum.isCompleteZonedDateTime()) {
      _stack.add(
        _query.getManager().getCriteriaBuilder().and(
          _query.getManager().getCriteriaBuilder().greaterThanOrEqualTo(
            minimum.zone(_field.select(_query), _query.getManager().getCriteriaBuilder()),
            minimum.toZonedDateTime()
          ),
          _query.getManager().getCriteriaBuilder().lessThanOrEqualTo(
            maximum.zone(_field.select(_query), _query.getManager().getCriteriaBuilder()),
            maximum.toZonedDateTime()
          )
        )
      );
    } else {
      final List<Predicate> predicates = new ArrayList<>();

      if (minimum.containsDatetime()) {
        predicates.add(
          _query.getManager().getCriteriaBuilder().and(
            _query.getManager().getCriteriaBuilder().greaterThanOrEqualTo(
              minimum.mask(_field.select(_query), _query.getManager().getCriteriaBuilder()),
              minimum.toZonedDateTime()
            ),
            _query.getManager().getCriteriaBuilder().lessThanOrEqualTo(
              maximum.mask(_field.select(_query), _query.getManager().getCriteriaBuilder()),
              maximum.toZonedDateTime()
            )
          )
        );
      }

      if (minimum.containsContext()) {
        Arrays.stream(PartialZonedDateTime.CONTEXT_FIELDS)
              .filter(minimum::isSupported)
              .map(
                field -> _query.getManager().getCriteriaBuilder().and(
                  _query.getManager().getCriteriaBuilder().greaterThanOrEqualTo(
                    minimum.select(
                      _field.select(_query),
                      _query.getManager().getCriteriaBuilder(),
                      field
                    ), minimum.getLong(field)
                  ),
                  _query.getManager().getCriteriaBuilder().lessThanOrEqualTo(
                    maximum.select(
                      _field.select(_query),
                      _query.getManager().getCriteriaBuilder(),
                      field
                    ),
                    maximum.getLong(field)
                  )
                )
               )
              .forEach(predicates::add);
      }

      _stack.add(
        _query.getManager().getCriteriaBuilder().and(
          predicates.toArray(new Predicate[predicates.size()])
        )
      );
    }
  }

  @FunctionalInterface
  private interface PredicateFactory
  {
    <Value extends Comparable<? super Value>> Predicate create (
      @NonNull final Expression<? extends Value> a, @NonNull final Value b
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
