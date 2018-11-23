package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.request.selection.APIRequestJoinSelectionParser;
import org.liara.api.request.selection.APIRequestSelectionParser;
import org.liara.api.request.selection.APIRequestSelectionParserFactory;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import java.time.ZonedDateTime;
import java.util.*;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EntityBasedSelectionConfigurationFactory
{
  @NonNull
  private final static WeakHashMap<@NonNull Class<?>, @NonNull EntityBasedSelectionConfiguration<?>> CONFIGURATIONS =
    new WeakHashMap<>();
  @NonNull
  private final        EntityManager                                                                 _entityManager;
  @NonNull
  private final        APIRequestSelectionParserFactory                                              _parserFactory;

  @Autowired
  public EntityBasedSelectionConfigurationFactory (
    @NonNull final EntityManager entityManager, @NonNull final APIRequestSelectionParserFactory parserFactory
  )
  {
    _entityManager = entityManager;
    _parserFactory = parserFactory;
  }

  public <Entity> @NonNull EntityBasedSelectionConfiguration<Entity> getConfigurationOf (
    @NonNull final Class<Entity> clazz
  )
  {
    return getConfigurationOf(_entityManager.getMetamodel().managedType(clazz));
  }

  public <Entity> @NonNull EntityBasedSelectionConfiguration<Entity> getConfigurationOf (
    @NonNull final ManagedType<Entity> entity
  )
  {
    if (!CONFIGURATIONS.containsKey(entity.getJavaType())) {
      CONFIGURATIONS.put(entity.getJavaType(), generateConfigurationFor(entity));
    }

    return (EntityBasedSelectionConfiguration<Entity>) CONFIGURATIONS.get(entity.getJavaType());
  }

  private <Entity> @NonNull EntityBasedSelectionConfiguration<?> generateConfigurationFor (
    @NonNull final EntityManager manager, @NonNull final Class<Entity> entity
  )
  { return generateConfigurationFor(manager.getMetamodel().entity(entity)); }

  private <Entity> @NonNull EntityBasedSelectionConfiguration<?> generateConfigurationFor (
    @NonNull final ManagedType<Entity> entityType
  )
  {
    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes = entityType.getAttributes();

    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestParser<Operator>> parsers =
      new HashMap<>();

    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestValidator> validators =
      new HashMap<>();

    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      if (attribute.isCollection()) {
        // do something
      } else {
        generateSingleAttributeConfiguration(attribute, parsers, validators);
      }
    }

    return new EntityBasedSelectionConfiguration<>(entityType.getJavaType(), parsers, validators);
  }

  private <Entity> void generateSingleAttributeConfiguration (
    @NonNull final Attribute<? super Entity, ?> attribute,
    @NonNull final Map<Attribute<? super Entity, ?>, APIRequestParser<Operator>> parsers,
    @NonNull final Map<Attribute<? super Entity, ?>, APIRequestValidator> validators
  )
  {
    if (attribute.isAssociation()) {
      parsers.put(attribute,
        _parserFactory.asJoinWith(attribute.getName(), getConfigurationOf(attribute.getJavaType()))
      );
    } else if (isEmbeddable(attribute.getJavaType())) {
      generateEmbededAttributeConfiguration(attribute, parsers, validators);
    } else {
      @NonNull final Optional<APIRequestSelectionParser> filter = rawAttributeToSelection(attribute);

      if (filter.isPresent()) {
        parsers.put(attribute, filter.get());
        validators.put(attribute, filter.get());
      }
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

  private <Entity> void generateEmbededAttributeConfiguration (
    @NonNull final Attribute<? super Entity, ?> attribute,
    @NonNull final Map<Attribute<? super Entity, ?>, APIRequestParser<Operator>> parsers,
    @NonNull final Map<Attribute<? super Entity, ?>, APIRequestValidator> validators
  )
  {
    @NonNull final APIRequestJoinSelectionParser parser = _parserFactory.asEmbedded(attribute.getName(),
      getConfigurationOf(attribute.getJavaType())
    );

    parsers.put(attribute, parser);
    validators.put(attribute, parser);
  }

  private <Entity> @NonNull Optional<APIRequestSelectionParser> rawAttributeToSelection (
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
