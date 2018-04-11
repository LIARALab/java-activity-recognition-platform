package org.liara.api.collection.filtering;

import java.util.Iterator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public class JoinBasedEntityFilter<Entity, Joined> implements EntityFilter<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final EntityFilter<Joined> _filter;
  
  public JoinBasedEntityFilter(
    @NonNull final String field,
    @NonNull final Iterable<EntityFilter<Joined>> filters
  )
  {
    _field = field;
    _filter = new ComposedEntityFilter<>(filters);
  }
  
  public JoinBasedEntityFilter(
    @NonNull final String field,
    @NonNull final Iterator<EntityFilter<Joined>> filters
  )
  {
    _field = field;
    _filter = new ComposedEntityFilter<>(filters);
  }
  
  public JoinBasedEntityFilter(
    @NonNull final String field,
    @NonNull final EntityFilter<Joined> filter
  )
  {
    _field = field;
    _filter = filter;
  }

  @Override
  public Predicate create (@NonNull final CriteriaBuilder builder, @NonNull final EntityCollectionQuery<Entity, ?> query) {
    return _filter.create(builder, query.joinCollection(_field));
  }
}
