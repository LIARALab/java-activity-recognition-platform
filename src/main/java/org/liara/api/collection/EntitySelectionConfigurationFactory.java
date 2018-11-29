package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.request.selection.APIRequestJoinSelectionParser;
import org.liara.api.request.selection.APIRequestSelectionParser;
import org.liara.api.request.selection.APIRequestSelectionParserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.WeakHashMap;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EntitySelectionConfigurationFactory
{
  @NonNull
  private final static WeakHashMap<@NonNull Class<?>, @NonNull EntityRequestConfiguration<?>> CONFIGURATIONS =
    new WeakHashMap<>();

  @NonNull
  private final EntityManager _entityManager;
  @NonNull
  private final APIRequestSelectionParserFactory _parserFactory;

  @Autowired
  public EntitySelectionConfigurationFactory (
    @NonNull final EntityManager entityManager, @NonNull final APIRequestSelectionParserFactory parserFactory
  )
  {
    _entityManager = entityManager;
    _parserFactory = parserFactory;
  }

  public <Entity> @NonNull EntityRequestConfiguration<Entity> getConfigurationOf (
    @NonNull final Class<Entity> clazz
  )
  {
    return getConfigurationOf(_entityManager.getMetamodel().managedType(clazz));
  }

  public <Entity> @NonNull EntityRequestConfiguration<Entity> getConfigurationOf (
    @NonNull final ManagedType<Entity> entity
  )
  {
    if (!CONFIGURATIONS.containsKey(entity.getJavaType())) {
      CONFIGURATIONS.put(entity.getJavaType(), generateConfigurationFor(entity));
    }

    return (EntityRequestConfiguration<Entity>) CONFIGURATIONS.get(entity.getJavaType());
  }

  private <Entity> @NonNull EntityRequestConfiguration<Entity> generateConfigurationFor (
    @NonNull final EntityManager manager, @NonNull final Class<Entity> entity
  )
  {
    return generateConfigurationFor(manager.getMetamodel().entity(entity));
  }

  private <Entity> @NonNull EntityRequestConfiguration<Entity> generateConfigurationFor (
    @NonNull final ManagedType<Entity> ManagedType
  )
  {
    @NonNull final EntityRequestConfiguration<Entity> configuration = new EntityRequestConfiguration<>(ManagedType);

    for (@NonNull final Attribute<? super Entity, ?> attribute : ManagedType.getAttributes()) {
      configureAttribute(configuration, attribute);
    }

    return configuration;
  }

  private <Entity> void configureAttribute (
    @NonNull final EntityRequestConfiguration<Entity> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    if (attribute.isCollection()) {
      configureCollection(configuration, attribute);
    } else if (attribute.isAssociation()) {
      configureAssociation(configuration, attribute);
    } else {
      configureField(configuration, attribute);
    }
  }

  private <Entity> void configureCollection (
    @NonNull final EntityRequestConfiguration<Entity> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    // @TODO
  }

  private <Entity> void configureAssociation (
    @NonNull final EntityRequestConfiguration<Entity> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    @NonNull final APIRequestJoinSelectionParser parser = _parserFactory.asJoinWith(attribute.getName(),
      getConfigurationOf(attribute.getJavaType())
    );

    configuration.setParser(attribute, parser);
    configuration.setValidator(attribute, parser);
  }

  private <Entity> void configureField (
    @NonNull final EntityRequestConfiguration<Entity> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    if (isEmbeddable(attribute.getJavaType())) {
      configureEmbeddableField(configuration, attribute);
    } else {
      configureRawField(configuration, attribute);
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

  private <Entity> void configureEmbeddableField (
    @NonNull final EntityRequestConfiguration<Entity> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    @NonNull final APIRequestJoinSelectionParser parser = _parserFactory.asEmbedded(
      attribute.getName(),
      getConfigurationOf(attribute.getJavaType())
    );

    configuration.setParser(attribute, parser);
    configuration.setValidator(attribute, parser);
  }


  private <Entity> void configureRawField (
    @NonNull final EntityRequestConfiguration<Entity> configuration,
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    @NonNull final Optional<APIRequestSelectionParser> filter = getRawFieldSelection(attribute);

    if (filter.isPresent()) {
      configuration.setValidator(attribute, filter.get());
      configuration.setParser(attribute, filter.get());
    }
  }

  private <Entity> @NonNull Optional<APIRequestSelectionParser> getRawFieldSelection (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    @NonNull final Class<?> attributeType = attribute.getJavaType();
    @Nullable final APIRequestSelectionParser result;

    if (Double.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asDouble(attribute.getName());
    } else if (Float.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asFloat(attribute.getName());
    } else if (Long.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asLong(attribute.getName());
    } else if (Integer.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asInteger(attribute.getName());
    } else if (Short.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asShort(attribute.getName());
    } else if (Byte.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asByte(attribute.getName());
    } else if (Boolean.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asBoolean(attribute.getName());
    } else if (ZonedDateTime.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asDateTime(attribute.getName());
    } else if (CharSequence.class.isAssignableFrom(attributeType)) {
      result = _parserFactory.asString(attribute.getName());
    } else {
      result = null;
    }

    return Optional.ofNullable(result);
  }
}
