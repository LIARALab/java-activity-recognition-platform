package org.liara.api.collection.ordering;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

public class JoinOrdering<Entity, Joined> implements Ordering<Entity>
{
  @NonNull
  private final String _join;
  
  @NonNull
  private final Ordering<Joined> _ordering;
  
  public JoinOrdering(
    @NonNull final String join, 
    @NonNull final Ordering<Joined> ordering
  ) {
    _join = join;
    _ordering = ordering;
  }

  @Override
  public List<Order> create (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    return _ordering.create(builder, query.joinCollection(_join));
  }

  public String getJoin () {
    return _join;
  }

  public Ordering<Joined> getOrdering () {
    return _ordering;
  }
}
