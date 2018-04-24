package org.liara.api.collection.transformation.grouping;

import java.util.Arrays;

import javax.persistence.Tuple;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.view.EntityCollectionAggregation;
import org.liara.api.collection.view.EntityCollectionGrouping;
import org.springframework.lang.NonNull;

import com.google.common.collect.ImmutableList;

public interface EntityCollectionGroupTransformation<Entity>
{
  public void apply (
    @NonNull final EntityCollectionMainQuery<Entity, Tuple> query
  );
  
  default public EntityCollectionGrouping<Entity> apply (
    @NonNull final EntityCollectionAggregation<Entity, ?> aggregation
  ) {
    return new EntityCollectionGrouping<>(aggregation, this);
  }
  
  default public EntityCollectionGroupTransformation<Entity> apply (
    @NonNull final EntityCollectionGroupTransformation<Entity> group
  ) {
    if (group instanceof EntityCollectionMultipleGroupingTransformation) {
      final ImmutableList.Builder<EntityCollectionGroupTransformation<Entity>> builder = ImmutableList.builder();
      builder.addAll((EntityCollectionMultipleGroupingTransformation<Entity>) group);
      builder.add(this);
      return new EntityCollectionMultipleGroupingTransformation<>(builder.build());
    } else {
      return new EntityCollectionMultipleGroupingTransformation<>(
        Arrays.asList(group, this)
      );
    }
  }
  
  default public EntityCollectionGrouping<Entity> apply (
    @NonNull final EntityCollectionGrouping<Entity> group
  ) {
    return new EntityCollectionGrouping<>(
      group.getAggregation(), 
      apply(group.getGroupTransformation())
    );
  }
}
