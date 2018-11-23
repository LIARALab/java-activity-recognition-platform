package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.request.ordering.APIRequestOrderingParser;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.ordering.Order;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;
import org.liara.selection.processor.ProcessorExecutor;

import javax.persistence.metamodel.Attribute;
import java.util.Map;

public final class EntityBasedOrderingConfiguration<Entity>
  implements CollectionRequestConfiguration
{
  @NonNull
  private final Class<Entity> _entity;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull ProcessorExecutor<Order>> _executors;

  EntityBasedOrderingConfiguration (
    @NonNull final Class<Entity> entity,
    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull ProcessorExecutor<Order>> executors
  )
  {
    _entity = entity;
    _executors = executors;
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    return APIRequestValidator.compose(new APIRequestOrderingParser(getExecutor()));
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    return APIRequestParser.compose(new APIRequestOrderingParser(getExecutor())).map(Composition::of);
  }

  public @NonNull ProcessorExecutor<Order> getExecutor () {
    return ProcessorExecutor.composition(_executors.values()
                                                   .toArray((ProcessorExecutor<Order>[]) new ProcessorExecutor[0]));
  }

  public @NonNull ProcessorExecutor<Order> getExecutor (@NonNull final Attribute attribute) {
    return _executors.get(attribute);
  }

  public @NonNull Class<Entity> getEntity () {
    return _entity;
  }
}
