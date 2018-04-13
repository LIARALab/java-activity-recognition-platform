package org.liara.api.collection.grouping;

import java.util.Collections;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public class EmptyGrouping<Entity> implements EntityGrouping<Entity>
{
  @Override
  public List<Selection<?>> createSelection (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    return Collections.emptyList();
  }
  
  @Override
  public List<Expression<?>> createGroupBy (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    return Collections.emptyList();
  }
}
