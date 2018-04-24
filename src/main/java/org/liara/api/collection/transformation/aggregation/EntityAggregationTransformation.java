package org.liara.api.collection.transformation.aggregation;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Selection;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.transformation.Transformation;
import org.liara.api.collection.view.EntityCollectionAggregation;
import org.springframework.lang.NonNull;

public interface EntityAggregationTransformation<Entity, AggregationResult>
       extends   Transformation<
                   EntityCollection<Entity>, 
                   EntityCollectionAggregation<Entity, AggregationResult>
                 >
{ 
  /**
   * Return the type of this aggregation.
   * 
   * @return The type of this aggregation.
   */
  public Class<AggregationResult> getAggregationType ();
  
  /**
   * Return the expected aggregation.
   * 
   * @param query Query to aggregate.
   * @return The expected aggregation.
   */
  public Selection<AggregationResult> aggregate (
    @NonNull final EntityCollectionMainQuery<Entity, ?> query
  );
  
  /**
   * Apply the current operator to the given query.
   * 
   * @param query Query to update.
   */
  default public void apply (
    @NonNull final EntityCollectionMainQuery<Entity, ?> query
  ) {
    final Selection<?> selection = query.getCriteriaQuery().getSelection();
    
    if (selection == null) {
      query.getCriteriaQuery().multiselect(aggregate(query));
    } else {
      final List<Selection<?>> selections = new ArrayList<>(selection.getCompoundSelectionItems());
      selections.add(selection);
      query.getCriteriaQuery().multiselect(selections);
    }
  }
  
  @Override
  public default EntityCollectionAggregation<Entity, AggregationResult> apply (
    @NonNull final EntityCollection<Entity> input
  ) {
    return new EntityCollectionAggregation<>(input, this);
  }
}
