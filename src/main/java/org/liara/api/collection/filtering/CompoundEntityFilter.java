package org.liara.api.collection.filtering;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.EntityCollectionQuery;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class CompoundEntityFilter<Entity> implements Iterable<EntityFilter<Entity>>, EntityFilter<Entity> 
{
  @NonNull
  private final Set<EntityFilter<Entity>> _filters = new HashSet<>();

  public CompoundEntityFilter () {

  }
  
  public CompoundEntityFilter (@NonNull final Iterable<EntityFilter<Entity>> filters) {
    Iterables.addAll(_filters, filters);
  }
  
  public CompoundEntityFilter (@NonNull final Iterator<EntityFilter<Entity>> filters) {
    Iterators.addAll(_filters, filters);
  }
  
  public CompoundEntityFilter (@NonNull final EntityFilter<Entity>... filters) {
    _filters.addAll(Arrays.asList(filters));
  }
  
  @Override
  public Iterator<EntityFilter<Entity>> iterator () {
    return _filters.iterator();
  } 

  public Predicate create (
    @NonNull final CriteriaBuilder builder,
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    
    return builder.and(
      _filters.stream().map(x -> x.create(builder, query))
                       .toArray(size -> new Predicate[size])
    );
  }

  public CompoundEntityFilter<Entity> add (@NonNull final EntityFilter<Entity> filter) {
    return new CompoundEntityFilter<>(Iterators.concat(_filters.iterator(), Iterators.singletonIterator(filter)));
  }
  
  public CompoundEntityFilter<Entity> merge (@NonNull final CompoundEntityFilter<Entity> filter) {
    return new CompoundEntityFilter<>(Iterators.concat(_filters.iterator(), filter.iterator()));
  }

  public CompoundEntityFilter<Entity> clear () {
    return new CompoundEntityFilter<>();
  }

  public boolean contains (@NonNull final ASTBasedEntityFilter<Entity, ?> filter) {
    return _filters.contains(filter);
  }

  public int size () {
    return _filters.size();
  }
}
