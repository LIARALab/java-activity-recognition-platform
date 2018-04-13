package org.liara.api.collection.grouping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class ComposedGrouping<Entity> implements EntityGrouping<Entity>
{
  @NonNull
  private final List<EntityGrouping<Entity>> _groupings = new ArrayList<>();
  
  public ComposedGrouping () {
    
  }
  
  public ComposedGrouping (
    @NonNull final Iterable<EntityGrouping<Entity>> groupings
  ) {
    Iterables.addAll(_groupings, groupings);
  }

  public ComposedGrouping (
    @NonNull final Iterator<EntityGrouping<Entity>> groupings
  ) {
    Iterators.addAll(_groupings, groupings);
  }
  
  public ComposedGrouping (
    @NonNull final EntityGrouping<Entity>... groupings
  ) {
    _groupings.addAll(Arrays.asList(groupings));
  }
  
  @Override
  public List<Selection<?>> createSelection (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    final List<Selection<?>> result = new ArrayList<>();
    
    for (final EntityGrouping<Entity> grouping : _groupings) {
      result.addAll(grouping.createSelection(builder, query));
    }
   
    return result;
  }
  
  @Override
  public List<Expression<?>> createGroupBy (
    @NonNull final CriteriaBuilder builder, 
    @NonNull final EntityCollectionQuery<Entity, Tuple> query
  ) {
    final List<Expression<?>> result = new ArrayList<>();
    
    for (final EntityGrouping<Entity> grouping : _groupings) {
      result.addAll(grouping.createGroupBy(builder, query));
    }
   
    return result;
  }
}
