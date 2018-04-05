package org.liara.api.collection.filtering;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;

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
    _filters.stream().forEach(x -> x.filter(builder, query));
  }

  public boolean add (EntityFieldFilter<Entity, ?> e) {
    return _filters.add(e);
  }

  public boolean addAll (Collection<? extends EntityFieldFilter<Entity, ?>> c) {
    return _filters.addAll(c);
  }

  public void clear () {
    _filters.clear();
  }

  public boolean contains (Object o) {
    return _filters.contains(o);
  }

  public boolean remove (Object o) {
    return _filters.remove(o);
  }

  public boolean removeAll (Collection<?> c) {
    return _filters.removeAll(c);
  }

  public int size () {
    return _filters.size();
  }
}
