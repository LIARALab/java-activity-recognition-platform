package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.request.ordering.APIRequestOrderingProcessor;
import org.liara.collection.operator.Operator;
import org.liara.selection.processor.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import java.util.WeakHashMap;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EntityOrderingConfigurationFactory
{
  @NonNull
  private final Metamodel _metamodel;

  @NonNull
  private final WeakHashMap<@NonNull Class<?>, @NonNull ProcessorParameterConfiguration> CONFIGURATIONS =
    new WeakHashMap<>();

  @Autowired
  public EntityOrderingConfigurationFactory (@NonNull final Metamodel metamodel) {
    _metamodel = metamodel;
  }

  public @NonNull ProcessorParameterConfiguration getConfigurationOf (
    @NonNull final Class<?> entity
  )
  {
    return getConfigurationOf(_metamodel.managedType(entity));
  }

  public @NonNull ProcessorParameterConfiguration getConfigurationOf (
    @NonNull final ManagedType<?> entity
  )
  {
    if (!CONFIGURATIONS.containsKey(entity.getJavaType())) {
      CONFIGURATIONS.put(entity.getJavaType(), generateConfigurationFor(entity));
    }

    return CONFIGURATIONS.get(entity.getJavaType());
  }

  private @NonNull ProcessorParameterConfiguration generateConfigurationFor (
    @NonNull final ManagedType<?> entity
  )
  {
    return new ProcessorParameterConfiguration(generateConfigurationFor(new RequestPath(), entity));
  }

  private @NonNull SimpleMapConfiguration<Processor<Operator>> generateConfigurationFor (
    @NonNull final RequestPath prefix, @NonNull final ManagedType<?> entity
  )
  {
    @NonNull final SimpleMapConfiguration<Processor<Operator>> configuration = new SimpleMapConfiguration<>();

    for (@NonNull final Attribute<?, ?> attribute : entity.getAttributes()) {
      if (!attribute.isCollection() && !attribute.isAssociation()) {
        if (isEmbeddable(attribute.getJavaType())) {
          configuration.set(attribute.getName(),
            generateConfigurationFor(prefix.concat(attribute.getName()),
              _metamodel.managedType(attribute.getJavaType())
            )
          );
        } else {
          configuration.set(attribute.getName(), generateFieldOrder(prefix, attribute));
        }
      }
    }

    return configuration;
  }

  private @NonNull Processor<Operator> generateFieldOrder (
    @NonNull final RequestPath prefix, @NonNull final Attribute<?, ?> attribute
  )
  {
    return new APIRequestOrderingProcessor(prefix.concat(attribute.getName()).toString());
  }

  private boolean isEmbeddable (@NonNull final Class<?> type) {
    try {
      _metamodel.embeddable(type);
      return true;
    } catch (@NonNull final IllegalArgumentException exception) {
      return false;
    }
  }
}
