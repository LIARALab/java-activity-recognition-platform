package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.rest.processor.aggregate.AggregationProcessor;
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
public class AdditionalLabelStateConfiguration
  implements AdditionalGroupingsFactory<LabelState>,
             AdditionalOrderingsFactory<LabelState>,
             AdditionalAggregationsFactory<LabelState>,
             AdditionalFiltersFactory<LabelState>
{
  @NonNull
  private final static String DATE_START = ":this.start";

  @NonNull
  private final static String DATE_END = ":this.end";

  @NonNull
  private final static String DURATION = (
    "(TIMESTAMPDIFF_MICROSECOND(:this.start, :this.end) / 1000)"
  );

  @NonNull
  private final FilterHandlerFactory _filterHandlerFactory;

  @Autowired
  public AdditionalLabelStateConfiguration (
    @NonNull final FilterHandlerFactory filterHandlerFactory
  ) {
    _filterHandlerFactory = filterHandlerFactory;
  }

  @Override
  public @NonNull ProcessorExecutor<Operator> getAdditionalAggregations () {
    return ProcessorExecutor.all(
      ProcessorExecutor.field(
        "duration", ProcessorExecutor.execute(
          new AggregationProcessor(DURATION)
        )
      )
    );
  }

  @Override
  public @NonNull RestRequestHandler getAdditionalFilters () {
    @NonNull final FilterHandler dateHandler = _filterHandlerFactory.createDatetimeInRange(
      DATE_START, DATE_END
    );

    @NonNull final FilterHandler durationHandler = _filterHandlerFactory.createDuration(
      DURATION
    );

    return RestRequestHandler.all(
      RestRequestHandler.parameter(
        "date",
        new StaticRestParameterHandler(dateHandler, dateHandler)
      ),
      RestRequestHandler.parameter(
        "duration",
        new StaticRestParameterHandler(durationHandler, durationHandler)
      )
    );
  }

  @Override
  public @NonNull ProcessorExecutor<Operator> getAdditionalGroupings () {
    return ProcessorExecutor.all();
  }

  @Override
  public @NonNull ProcessorExecutor<Operator> getAdditionalOrderings () {
    return ProcessorExecutor.all(
      ProcessorExecutor.field(
        "date", ProcessorExecutor.execute(
          new OrderingProcessor(DATE_START)
        )
      ),
      ProcessorExecutor.field(
        "duration", ProcessorExecutor.execute(
          new OrderingProcessor(DURATION)
        )
      )
    );
  }

  @Override
  public @NonNull Class<LabelState> getEntityClass () {
    return LabelState.class;
  }
}
