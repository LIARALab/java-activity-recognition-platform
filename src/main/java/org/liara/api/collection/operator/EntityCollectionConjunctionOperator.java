package org.liara.api.collection.operator;

import java.util.Iterator;
import java.util.List;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

import com.google.common.collect.ImmutableList;

/**
 * An operator that is a list of operators to conjugate.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
   * @param operators Operators to conjugate.
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
}
