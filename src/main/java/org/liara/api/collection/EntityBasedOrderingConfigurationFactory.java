package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.request.ordering.APIRequestJoinOrderingExecutor;
import org.liara.api.request.ordering.APIRequestOrderingProcessor;
import org.liara.collection.operator.ordering.Order;
import org.liara.selection.processor.ProcessorExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EntityBasedOrderingConfigurationFactory
{
  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final WeakHashMap<@NonNull Class<?>, @NonNull EntityBasedOrderingConfiguration<?>> CONFIGURATIONS =
    new WeakHashMap<>();


  @Autowired
  public EntityBasedOrderingConfigurationFactory (@NonNull final EntityManager entityManager) {
    _entityManager = entityManager;
  }

  public <Entity> @NonNull EntityBasedOrderingConfiguration<Entity> getConfigurationOf (
    @NonNull final Class<Entity> entity
  )
  {
    return getConfigurationOf(_entityManager.getMetamodel().managedType(entity));
  }

  public <Entity> @NonNull EntityBasedOrderingConfiguration<Entity> getConfigurationOf (
    @NonNull final ManagedType<Entity> entity
  )
  {
    if (!CONFIGURATIONS.containsKey(entity.getJavaType())) {
      CONFIGURATIONS.put(entity.getJavaType(), generateConfigurationFor(entity));
    }

    return (EntityBasedOrderingConfiguration<Entity>) CONFIGURATIONS.get(entity.getJavaType());
  }

  private <Entity> @NonNull EntityBasedOrderingConfiguration<Entity> generateConfigurationFor (
    @NonNull final ManagedType<Entity> entity
  )
  {
    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes = entity.getAttributes();

    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull ProcessorExecutor<Order>> executors =
      new HashMap<>();

    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      if (!attribute.isCollection()) {
        executors.put(attribute, generateSingleAttributeConfiguration(attribute));
      }
    }

    return new EntityBasedOrderingConfiguration<>(entity.getJavaType(), executors);
  }

  private <Entity> @NonNull ProcessorExecutor<Order> generateSingleAttributeConfiguration (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    if (attribute.isAssociation() || isEmbeddable(attribute.getJavaType())) {
      return new APIRequestJoinOrderingExecutor(attribute.getName(), getConfigurationOf(attribute.getJavaType()));
    } else {
      return ProcessorExecutor.executeIf(
        ProcessorExecutor.execute(new APIRequestOrderingProcessor(attribute.getName())),
        call -> call.getFullIdentifier().equals(attribute.getName())
      );
    }
  }

  private boolean isEmbeddable (@NonNull final Class<?> type) {
    try {
      _entityManager.getMetamodel().embeddable(type);
      return true;
    } catch (@NonNull final IllegalArgumentException exception) {
      return false;
    }
  }
}
