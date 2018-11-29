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
import java.util.Set;
import java.util.WeakHashMap;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EntityBasedOrderingConfigurationFactory
{
  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final WeakHashMap<@NonNull Class<?>, @NonNull EntityProcessorConfiguration<?, Order>> CONFIGURATIONS =
    new WeakHashMap<>();

  @Autowired
  public EntityBasedOrderingConfigurationFactory (@NonNull final EntityManager entityManager) {
    _entityManager = entityManager;
  }

  public <Entity> @NonNull EntityProcessorConfiguration<Entity, Order> getConfigurationOf (
    @NonNull final Class<Entity> entity
  )
  {
    return getConfigurationOf(_entityManager.getMetamodel().managedType(entity));
  }

  public <Entity> @NonNull EntityProcessorConfiguration<Entity, Order> getConfigurationOf (
    @NonNull final ManagedType<Entity> entity
  )
  {
    if (!CONFIGURATIONS.containsKey(entity.getJavaType())) {
      CONFIGURATIONS.put(entity.getJavaType(), generateConfigurationFor(entity));
    }

    return (EntityProcessorConfiguration<Entity, Order>) CONFIGURATIONS.get(entity.getJavaType());
  }

  private <Entity> @NonNull EntityProcessorConfiguration<Entity, Order> generateConfigurationFor (
    @NonNull final ManagedType<Entity> entity
  )
  {
    @NonNull final EntityProcessorConfiguration<Entity, Order> configuration = new EntityProcessorConfiguration<>(
      "orderby",
      entity
    );

    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes = entity.getAttributes();
    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      if (!attribute.isCollection()) {
        configureAttribute(configuration, attribute);
      }
    }

    return configuration;
  }

  private <Entity> void configureAttribute (
    @NonNull final EntityProcessorConfiguration<Entity, Order> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    if (attribute.isAssociation() || isEmbeddable(attribute.getJavaType())) {
      configureAssociationAttribute(configuration, attribute);
    } else {
      configureRawAttribute(configuration, attribute);
    }
  }

  private <Entity> void configureRawAttribute (
    @NonNull final EntityProcessorConfiguration<Entity, Order> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    configuration.setProcessor(attribute, ProcessorExecutor.executeIf(
        ProcessorExecutor.execute(new APIRequestOrderingProcessor(attribute.getName())),
        call -> call.getFullIdentifier().equals(attribute.getName())
    ));
  }

  private <Entity> void configureAssociationAttribute (
    @NonNull final EntityProcessorConfiguration<Entity, Order> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    configuration.setProcessor(attribute,
      new APIRequestJoinOrderingExecutor(attribute.getName(), getConfigurationOf(attribute.getJavaType()))
    );
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
