package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.rest.processor.aggregate.AggregationProcessor;
import org.liara.rest.processor.group.GroupingProcessor;
import org.liara.rest.processor.order.OrderingProcessor;
import org.liara.rest.request.filtering.FilterHandler;
import org.liara.rest.request.filtering.FilterHandlerFactory;
import org.liara.rest.request.handler.RestRequestHandler;
import org.liara.rest.request.handler.StaticRestParameterHandler;
import org.liara.rest.request.jpa.AdditionalAggregationsFactory;
import org.liara.rest.request.jpa.AdditionalFiltersFactory;
import org.liara.rest.request.jpa.AdditionalGroupingsFactory;
import org.liara.rest.request.jpa.AdditionalOrderingsFactory;
import org.liara.selection.processor.ProcessorExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class AdditionalApplicationEntityConfiguration
  implements AdditionalGroupingsFactory<ApplicationEntity>,
             AdditionalOrderingsFactory<ApplicationEntity>,
             AdditionalAggregationsFactory<ApplicationEntity>,
             AdditionalFiltersFactory<ApplicationEntity>
{
  @NonNull
  private final static String UUID = ":this.universalUniqueIdentifier";

  @NonNull
  private final FilterHandlerFactory _filterHandlerFactory;

  @Autowired
  public AdditionalApplicationEntityConfiguration (
    @NonNull final FilterHandlerFactory filterHandlerFactory
  ) {
    _filterHandlerFactory = filterHandlerFactory;
  }

  @Override
  public @NonNull ProcessorExecutor<Operator> getAdditionalAggregations () {
    return ProcessorExecutor.all(
      ProcessorExecutor.field(
        "uuid", ProcessorExecutor.execute(
          new AggregationProcessor(UUID)
        )
      )
    );
  }

  @Override
  public @NonNull RestRequestHandler getAdditionalFilters () {
    @NonNull final FilterHandler uuidFilterHandler = _filterHandlerFactory.createString(UUID);

    return RestRequestHandler.all(
      RestRequestHandler.parameter(
        "uuid",
        new StaticRestParameterHandler(uuidFilterHandler, uuidFilterHandler)
      )
    );
  }

  @Override
  public @NonNull ProcessorExecutor<Operator> getAdditionalGroupings () {
    return ProcessorExecutor.all(
      ProcessorExecutor.field(
        "uuid", ProcessorExecutor.execute(
          new GroupingProcessor(UUID)
        )
      )
    );
  }

  @Override
  public @NonNull ProcessorExecutor<Operator> getAdditionalOrderings () {
    return ProcessorExecutor.all(
      ProcessorExecutor.field(
        "uuid", ProcessorExecutor.execute(
          new OrderingProcessor(UUID)
        )
      )
    );
  }

  @Override
  public @NonNull Class<ApplicationEntity> getEntityClass () {
    return ApplicationEntity.class;
  }
}
