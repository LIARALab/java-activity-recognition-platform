package org.liara.api.collection.filtering;

import java.util.Iterator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubQuery;
import org.liara.api.criteria.CustomCollectionRelation;
import org.springframework.lang.NonNull;

public class CustomHavingBasedEntityFilter<Entity, Joined> implements EntityFilter<Entity>
{
  @NonNull
  private final CustomCollectionRelation<Entity, Joined> _relation;
  
  @NonNull
  private final Class<Joined> _joined;
  
  @NonNull
  private final EntityFilter<Joined> _filter;
  
  public CustomHavingBasedEntityFilter(
    @NonNull final CustomCollectionRelation<Entity, Joined> relation,
    @NonNull final Class<Joined> joined,
    @NonNull final Iterable<EntityFilter<Joined>> filters
  )
  {
    _relation = relation;
    _joined = joined;
    _filter = new ComposedEntityFilter<>(filters);
  }
  
  public CustomHavingBasedEntityFilter(
    @NonNull final CustomCollectionRelation<Entity, Joined> relation,
    @NonNull final Class<Joined> joined,
    @NonNull final Iterator<EntityFilter<Joined>> filters
  )
  {
    _relation = relation;
    _joined = joined;
    _filter = new ComposedEntityFilter<>(filters);
  }
  
  public CustomHavingBasedEntityFilter(
    @NonNull final CustomCollectionRelation<Entity, Joined> relation,
    @NonNull final Class<Joined> joined,
    @NonNull final EntityFilter<Joined> filter
  )
  {
    _relation = relation;
    _joined = joined;
    _filter = filter;
  }

  @Override
  public Predicate create (@NonNull final CriteriaBuilder builder, @NonNull final EntityCollectionQuery<Entity, ?> query) {
    final EntityCollectionSubQuery<Entity, ?, Joined, Joined> subQuery = query.subquery(_joined, _joined);
    subQuery.select(subQuery.getEntity());
    
    _relation.apply(builder, query, subQuery);
    _filter.filter(builder, subQuery);

    return builder.exists(subQuery);
  }
}
