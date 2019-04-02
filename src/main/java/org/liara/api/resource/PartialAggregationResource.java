package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.collection.Collection;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.aggregate.Aggregate;
import org.liara.rest.processor.ProcessorHandler;
import org.liara.rest.request.handler.RestRequestHandler;

import java.util.ArrayList;
import java.util.List;

public class PartialAggregationResource<Entity extends ApplicationEntity>
  extends AggregationResource<Entity>
{
  @NonNull
  private final List<Aggregate> _aggregations;

  public PartialAggregationResource (
    @NonNull final List<Aggregate> aggregations,
    @NonNull final CollectionResource<Entity> collection,
    @NonNull final AggregationResourceBuilder builder
  ) {
    super(collection, builder);
    _aggregations = new ArrayList<>(aggregations);
  }

  @Override
  protected @NonNull RestRequestHandler getRequestConfiguration () {
    return RestRequestHandler.all(
      getResourceFilteringHandler(),
      RestRequestHandler.parameter("groupby", new ProcessorHandler<>(getResourceGroupingHandler()))
    );
  }

  @Override
  public @NonNull Collection<Entity> getCollection () {
    return Composition.of(_aggregations).apply(super.getCollection()).collectionOf(getModelClass());
  }
}
