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
package org.liara.api.collection.transformation.operator;

import java.util.Iterator;
import java.util.List;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * An operator that is a list of operators to conjugate.
 * 
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 *
 * @param <Entity> Targeted entity of this operator.
 */
public class EntityCollectionConjunctionOperator<Entity> 
       implements EntityCollectionOperator<Entity>, 
                  Iterable<EntityCollectionOperator<Entity>>
{
  @NonNull
  private final List<EntityCollectionOperator<Entity>> _operators;

  /**
   * Create an empty conjunction operator. (Do nothing).
   */
  public EntityCollectionConjunctionOperator () {
    _operators = ImmutableList.of();
  }
  
  /**
   * Create a conjunction of the given operators (Preserve the order)
   * 
   * @param operators Operators to conjugate.
   */
  public EntityCollectionConjunctionOperator (
    @NonNull final Iterator<EntityCollectionOperator<Entity>> operators
  ) {
    _operators = ImmutableList.copyOf(operators);
  }
  
  /**
   * Create a conjunction of the given operators (Preserve the order)
   * 
   * @param operators Operators to conjugate.
   */
  public EntityCollectionConjunctionOperator (
    @NonNull final Iterable<EntityCollectionOperator<Entity>> operators
  ) {
    _operators = ImmutableList.copyOf(operators);
  }
  
  /**
   * Create a conjunction of the given operators (Preserve the order)
   * 
   * @param operators Operators to conjugate.
   */
  public EntityCollectionConjunctionOperator (
    @NonNull final EntityCollectionOperator<Entity>[] operators
  ) {
    _operators = ImmutableList.copyOf(operators);
  }
  
  /**
   * Create a conjunction of the given operators (Preserve the order)
   * 
   * @param operators Operators to conjugate.
   */
  public EntityCollectionConjunctionOperator (
    @NonNull final ImmutableList<EntityCollectionOperator<Entity>> operators
  ) {
    _operators = operators;
  }
  
  /**
   * Create a conjunction of the given operators (Preserve the order)
   * 
   * @param operator Operators to conjugate.
   */
  public EntityCollectionConjunctionOperator (
    @NonNull final EntityCollectionOperator<Entity> operator
  ) {
    if (operator instanceof EntityCollectionConjunctionOperator) {
      final EntityCollectionConjunctionOperator<Entity> conjunction = (EntityCollectionConjunctionOperator<Entity>) operator;
      _operators = ImmutableList.copyOf(conjunction);
    } else {
      _operators = ImmutableList.of(operator);
    }
  }
  
  
  /**
   * @see EntityCollectionOperator#apply(EntityCollectionQuery)
   */
  @Override
  public void apply (@NonNull final EntityCollectionQuery<Entity, ?> query) {
    for (final EntityCollectionOperator<Entity> operator : _operators) {
      operator.apply(query);
    }
  }

  /**
   * @see Iterable#iterator()
   */
  @Override
  public Iterator<EntityCollectionOperator<Entity>> iterator () {
    return _operators.iterator();
  }
  
  /**
   * Return conjuged operators.
   * 
   * @return Conjuged operators.
   */
  public List<EntityCollectionOperator<Entity>> getOperators () {
    return _operators;
  }
  
  public EntityCollectionConjunctionOperator<Entity> conjugate (
    @NonNull final EntityCollectionOperator<Entity> operator
  ) {
    final ImmutableList.Builder<EntityCollectionOperator<Entity>> builder = ImmutableList.builder();
    return new EntityCollectionConjunctionOperator<>(
      builder.addAll(_operators).add(operator).build()
    );
  }

  @Override
  public String toString () {
    StringBuilder builder = new StringBuilder();
    builder.append("EntityCollectionConjunctionOperator [");
    if (_operators != null) {
      builder.append("_operators=");
      builder.append(_operators);
    }
    builder.append("]");
    return builder.toString();
  }
}
