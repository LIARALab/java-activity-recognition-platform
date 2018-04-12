package org.liara.api.collection.ordering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class ComposedOrdering<Entity> implements Ordering<Entity>, Iterable<Ordering<Entity>>
{
  @NonNull
  private final List<Ordering<Entity>> _orderings = new ArrayList<>();

  public ComposedOrdering () {
    
  }
  
  public ComposedOrdering (@NonNull final Iterable<Ordering<Entity>> ordering) {
    Iterables.addAll(_orderings, ordering);
  }
  
  public ComposedOrdering (@NonNull final Iterator<Ordering<Entity>> ordering) {
    Iterators.addAll(_orderings, ordering);
  }
  
  public ComposedOrdering (@NonNull final Ordering<Entity>... ordering) {
    _orderings.addAll(Arrays.asList(ordering));
  }
  
  @Override
  public Iterator<Ordering<Entity>> iterator () {
    return _orderings.iterator();
  }
 
  @Override
  public List<Order> create (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    final List<Order> result = new ArrayList<>();
    
    for (final Ordering<Entity> ordering : _orderings) {
      result.addAll(ordering.create(builder, query));
    }
    
    return result;
  }
}
