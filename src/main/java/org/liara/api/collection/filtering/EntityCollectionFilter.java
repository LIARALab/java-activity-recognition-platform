package org.liara.api.collection.filtering;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.EntityCollectionQuery;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class EntityCollectionFilter<Entity> implements Iterable<EntityFieldFilter<Entity, ?>>
{
  @NonNull
  private final Set<EntityFieldFilter<Entity, ?>> _filters = new HashSet<>();

  public EntityCollectionFilter () {

  }
  
  public EntityCollectionFilter (@NonNull final Iterable<EntityFieldFilter<Entity, ?>> filters) {
    Iterables.addAll(_filters, filters);
  }
  
  public EntityCollectionFilter (@NonNull final Iterator<EntityFieldFilter<Entity, ?>> filters) {
    Iterators.addAll(_filters, filters);
  }
  
  public EntityCollectionFilter (@NonNull final EntityFieldFilter<Entity, ?>... filters) {
    _filters.addAll(Arrays.asList(filters));
  }
  
  @Override
  public Iterator<EntityFieldFilter<Entity, ?>> iterator () {
    return _filters.iterator();
  } 

  public void filter (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    query.where(visit(builder, query));
  }
  
  public Predicate visit (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    
    return builder.and(
      _filters.stream().map(x -> x.visit(builder, query))
                       .toArray(size -> new Predicate[size])
    );
  }

  public <Field> EntityCollectionFilter<Entity> add (@NonNull final EntityFieldFilter<Entity, Field> filter) {
    return new EntityCollectionFilter<>(Iterators.concat(_filters.iterator(), Iterators.singletonIterator(filter)));
  }
  
  public <Field> EntityCollectionFilter<Entity> merge (@NonNull final EntityCollectionFilter<Entity> filter) {
    return new EntityCollectionFilter<>(Iterators.concat(_filters.iterator(), filter.iterator()));
  }

  public EntityCollectionFilter<Entity> clear () {
    return new EntityCollectionFilter<>();
  }

  public boolean contains (@NonNull final EntityFieldFilter<Entity, ?> filter) {
    return _filters.contains(filter);
  }

  public int size () {
    return _filters.size();
  }
}
