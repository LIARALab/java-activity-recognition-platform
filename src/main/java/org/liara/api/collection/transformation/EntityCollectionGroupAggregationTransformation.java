package org.liara.api.collection.transformation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class      EntityCollectionGroupAggregationTransformation<Input>
       implements Transformation<Input, Tuple>
{
  @NonNull
  private final EntityFieldSelector<Input, Expression<?>> _selector;

  public EntityCollectionGroupAggregationTransformation (
    @NonNull final EntityFieldSelector<Input, Expression<?>> selector
  ) {
    _selector = selector;
  }

  @Override
  public CriteriaQuery<Tuple> transformCriteria (
    @NonNull final EntityCollectionQuery<Input, Tuple> collectionQuery, 
    @NonNull final CriteriaQuery<Tuple> query
  ) {    
    final List<Selection<?>> selections = new ArrayList<>(
      query.getSelection().getCompoundSelectionItems()
    );
    
    selections.add(_selector.select(collectionQuery));
    
    query.multiselect(selections);
    
    return query;
  }
}
