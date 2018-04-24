package org.liara.api.collection.transformation.grouping;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class      EntityCollectionFieldGroupingTransformation<Entity>
       implements EntityCollectionGroupTransformation<Entity>
{
  @NonNull
  private final EntityFieldSelector<Entity, Expression<?>> _field;
  
  public EntityCollectionFieldGroupingTransformation (
    @NonNull final EntityFieldSelector<Entity, Expression<?>> field
  ) {
    _field = field;
  }
  
  @Override
  public void apply (@NonNull final EntityCollectionMainQuery<Entity, Tuple> query) {
    final Expression<?> group = _field.select(query);
    
    query.andGroupBy(group);
    
    final List<Selection<?>> selections = new ArrayList<>(
      query.getCriteriaQuery().getSelection().getCompoundSelectionItems()
    );
 
    selections.add(group);
    query.getCriteriaQuery().multiselect(selections);
  }

}
