package org.liara.api.collection.grouping;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public class JoinGrouping<Entity, Joined> implements EntityGrouping<Entity>
{
  @NonNull
  private final String _join;
  
  @NonNull
  private final EntityGrouping<Joined> _grouping;
  
  public JoinGrouping (
    @NonNull final String join, 
    @NonNull final EntityGrouping<Joined> grouping
  ) {
    _join = join;
    _grouping = grouping;
  }

  @Override
  public List<Selection<?>> createSelection (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    return _grouping.createSelection(builder, query.joinCollection(_join));
  }

  @Override
  public List<Expression<?>> createGroupBy (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    return _grouping.createGroupBy(builder, query.joinCollection(_join));
  }
}
