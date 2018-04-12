package org.liara.api.collection.filtering;

import java.util.Iterator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubQuery;
import org.springframework.lang.NonNull;

public class HavingBasedEntityFilter<Entity, Joined> implements EntityFilter<Entity>
{
  @NonNull
  private final String _field;
  
  @NonNull
  private final EntityFilter<Joined> _filter;
  
  public HavingBasedEntityFilter(
    @NonNull final String field,
    @NonNull final Iterable<EntityFilter<Joined>> filters
  )
  {
    _field = field;
    _filter = new ComposedEntityFilter<>(filters);
  }
  
  public HavingBasedEntityFilter(
    @NonNull final String field,
    @NonNull final Iterator<EntityFilter<Joined>> filters
  )
  {
    _field = field;
    _filter = new ComposedEntityFilter<>(filters);
  }
  
  public HavingBasedEntityFilter(
    @NonNull final String field,
    @NonNull final EntityFilter<Joined> filter
  )
  {
    _field = field;
    _filter = filter;
  }

  @Override
  public Predicate create (@NonNull final CriteriaBuilder builder, @NonNull final EntityCollectionQuery<Entity, ?> query) {
    final Class<Entity> entity = query.getEntity().getModel().getBindableJavaType();
    final EntityCollectionSubQuery<Entity, ?, Entity, Entity> subQuery = query.subquery(entity, entity);
    subQuery.select(subQuery.getEntity());
    
    _filter.filter(builder, subQuery.joinCollection(_field));

    return query.getEntity().in(subQuery);
  }
}
