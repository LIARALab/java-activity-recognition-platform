package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.request.ordering.APIRequestJoinOrderingExecutor;
import org.liara.api.request.ordering.APIRequestOrderingParser;
import org.liara.api.request.ordering.APIRequestOrderingProcessor;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.ordering.Order;
import org.liara.processor.ProcessorExecutor;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class EntityBasedOrderingConfiguration<Entity>
  implements CollectionRequestConfiguration<Entity>
{
  @NonNull
  private final static WeakHashMap<@NonNull Class<?>, @NonNull EntityBasedOrderingConfiguration<?>> CONFIGURATIONS =
    new WeakHashMap<>();

  @NonNull
  private final Class<Entity> _entity;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull ProcessorExecutor<Order>> _executors;

  public static <Entity> @NonNull EntityBasedOrderingConfiguration<Entity> getConfigurationOf (
    @NonNull final EntityManager manager, @NonNull final Class<Entity> entity
  )
  {
    if (!CONFIGURATIONS.containsKey(entity)) {
      CONFIGURATIONS.put(entity, generateConfigurationFor(manager, entity));
    }

    return (EntityBasedOrderingConfiguration<Entity>) CONFIGURATIONS.get(entity);
  }

  private static <Entity> @NonNull EntityBasedOrderingConfiguration<?> generateConfigurationFor (
    @NonNull final EntityManager manager, @NonNull final Class<Entity> entity
  )
  {
    @NonNull final EntityType<Entity>                         entityType = manager.getMetamodel().entity(entity);
    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes = entityType.getAttributes();

    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull ProcessorExecutor<Order>> executors =
      new HashMap<>();

    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      if (!attribute.isCollection()) {
        executors.put(attribute, generateSingleAttributeConfiguration(manager, attribute));
      }
    }

    return new EntityBasedOrderingConfiguration<>(entity, executors);
  }

  private static <Entity> @NonNull ProcessorExecutor<Order> generateSingleAttributeConfiguration (
    @NonNull final EntityManager entityManager, @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    if (attribute.isAssociation()) {
      return new APIRequestJoinOrderingExecutor(attribute.getName(), entityManager, attribute.getJavaType());
    } else {
      return ProcessorExecutor.executeIf(
        ProcessorExecutor.execute(new APIRequestOrderingProcessor(attribute.getName())),
        call -> call.getFullIdentifier().equals(attribute.getName())
      );
    }
  }

  private EntityBasedOrderingConfiguration (
    @NonNull final Class<Entity> entity,
    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull ProcessorExecutor<Order>> executors
  )
  {
    _entity = entity;
    _executors = executors;
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    return APIRequestValidator.composse(new APIRequestOrderingParser(getExecutor()));
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
