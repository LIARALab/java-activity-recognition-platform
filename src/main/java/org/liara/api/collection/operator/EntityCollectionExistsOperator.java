package org.liara.api.collection.operator;

import java.util.Collection;

import javax.persistence.criteria.Path;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubquery;
import org.liara.api.collection.query.relation.EntityRelation;
import org.springframework.lang.NonNull;

public class EntityCollectionExistsOperator<Entity, Joined> implements EntityCollectionOperator<Entity>
{
  @NonNull
  private final Class<Joined> _type;

  @NonNull
  private final EntityRelation<Entity, Joined> _relation;

  @NonNull
  private final EntityCollectionOperator<Joined> _operator;
  
  public EntityCollectionExistsOperator(
    @NonNull final Class<Joined> type,
    @NonNull final EntityRelation<Entity, Joined> relation,
    @NonNull final EntityCollectionOperator<Joined> operator
  )
  {
    _type = type;
    _relation = relation;
    _operator = operator;
  }

  @Override
  public void apply (@NonNull final EntityCollectionQuery<Entity> query) {
    final EntityCollectionSubquery<Joined, Joined> subquery = query.subquery(_type, _type);
    _relation.apply(query, subquery);
    _operator.apply(subquery);
  }
}
