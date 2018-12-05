package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.request.filtering.APIRequestFilterParser;
import org.liara.api.request.filtering.APIRequestFilterParserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.WeakHashMap;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EntityFilteringConfigurationFactory
{
  @NonNull
  private final static WeakHashMap<@NonNull Class<?>, @NonNull RequestParameterMapConfiguration> CONFIGURATIONS =
    new WeakHashMap<>();

  @NonNull
  private final Metamodel _metamodel;

  @NonNull
  private final APIRequestFilterParserFactory _parserFactory;

  @Autowired
  public EntityFilteringConfigurationFactory (
    @NonNull final Metamodel metamodel, @NonNull final APIRequestFilterParserFactory parserFactory
  )
  {
    _metamodel = metamodel;
    _parserFactory = parserFactory;
  }

  public @NonNull RequestParameterMapConfiguration getConfigurationOf (@NonNull final Class<?> clazz) {
    return getConfigurationOf(_metamodel.managedType(clazz));
  }

  public @NonNull RequestParameterMapConfiguration getConfigurationOf (@NonNull final ManagedType<?> entity) {
    if (!CONFIGURATIONS.containsKey(entity.getJavaType())) {
      CONFIGURATIONS.put(entity.getJavaType(), generateConfigurationFor(entity));
    }

    return CONFIGURATIONS.get(entity.getJavaType());
  }

  private @NonNull RequestParameterMapConfiguration generateConfigurationFor (
    @NonNull final ManagedType<?> managedType
  )
  {
    return new RequestParameterMapConfiguration(generateConfigurationFor(new RequestPath(), managedType));
  }

  private @NonNull SimpleMapConfiguration generateConfigurationFor (
    @NonNull final RequestPath prefix, @NonNull final ManagedType<?> managedType
  )
  {
    @NonNull final SimpleMapConfiguration configuration = new SimpleMapConfiguration();

    for (@NonNull final Attribute<?, ?> attribute : managedType.getAttributes()) {
      if (!attribute.isCollection() && !attribute.isAssociation()) {
        if (isEmbeddable(attribute.getJavaType())) {
          configuration.set(
            attribute.getName(),
            generateConfigurationFor(prefix.concat(attribute.getName()),
              _metamodel.managedType(attribute.getJavaType())
            )
          );
        } else {
          configuration.set(attribute.getName(), generateFieldConfiguration(prefix, attribute));
        }
      }
    }

    return configuration;
  }

  private @NonNull RequestParameterConfiguration generateFieldConfiguration (
    @NonNull final RequestPath prefix, @NonNull final Attribute<?, ?> attribute
  )
  {
    @NonNull final APIRequestFilterParser filter = getRawFieldSelection(prefix, attribute);
    return new SimpleRequestParameterConfiguration(filter, filter);
  }

  private boolean isEmbeddable (@NonNull final Class<?> type) {
    try {
      _metamodel.embeddable(type);
      return true;
    } catch (@NonNull final IllegalArgumentException exception) {
      return false;
    }
  }

  private @NonNull APIRequestFilterParser getRawFieldSelection (
    @NonNull final RequestPath prefix, @NonNull final Attribute<?, ?> attribute
  )
  {
    @NonNull final Class<?> attributeType = attribute.getJavaType();
    @NonNull final String fieldName       = prefix.concat(attribute.getName()).toString();

    if (Double.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createDouble(fieldName);
    } else if (Float.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createFloat(fieldName);
    } else if (Long.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createLong(fieldName);
    } else if (Integer.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createInteger(fieldName);
    } else if (Short.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createShort(fieldName);
    } else if (Byte.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createByte(fieldName);
    } else if (Boolean.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createBoolean(fieldName);
    } else if (ZonedDateTime.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createDateTime(fieldName);
    } else if (CharSequence.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createString(fieldName);
    } else if (UUID.class.isAssignableFrom(attributeType)) {
      return _parserFactory.createString(fieldName);
    } else {
      throw new Error("Unhandled raw type " + attributeType.toString());
    }
  }
}
