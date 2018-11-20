package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.request.selection.APIRequestSelectionParser;
import org.liara.api.request.selection.APIRequestSelectionParserFactory;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import java.time.ZonedDateTime;
import java.util.*;

public final class EntityBasedSelectionConfiguration<Entity>
  implements CollectionRequestConfiguration<Entity>
{
  @NonNull
  private final static WeakHashMap<@NonNull Class<?>, @NonNull EntityBasedSelectionConfiguration<?>> CONFIGURATIONS =
    new WeakHashMap<>();

  @NonNull
  private final Class<Entity> _entity;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestParser<Operator>> _parsers;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestValidator> _validators;

  public static <Entity> @NonNull EntityBasedSelectionConfiguration<Entity> getConfigurationOf (
    @NonNull final EntityManager manager, @NonNull final Class<Entity> entity
  )
  {
    if (!CONFIGURATIONS.containsKey(entity)) {
      CONFIGURATIONS.put(entity, generateConfigurationFor(manager, entity));
    }

    return (EntityBasedSelectionConfiguration<Entity>) CONFIGURATIONS.get(entity);
  }

  private static <Entity> @NonNull EntityBasedSelectionConfiguration<?> generateConfigurationFor (
    @NonNull final EntityManager manager, @NonNull final Class<Entity> entity
  )
  {
    @NonNull final EntityType<Entity>                         entityType = manager.getMetamodel().entity(entity);
    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes = entityType.getAttributes();

    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestParser<Operator>> parsers =
      new HashMap<>();

    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestValidator> validators =
      new HashMap<>();

    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      if (attribute.isCollection()) {
        generatePluralAttributeConfiguration((PluralAttribute) attribute, parsers, validators);
      } else {
        generateSingleAttributeConfiguration(attribute, parsers, validators);
      }
    }

    return new EntityBasedSelectionConfiguration<>(entity, parsers, validators);
  }

  private static <Entity> void generatePluralAttributeConfiguration (
    @NonNull final PluralAttribute attribute,
    @NonNull final Map<Attribute<? super Entity, ?>, APIRequestParser<Operator>> parsers,
    @NonNull final Map<Attribute<? super Entity, ?>, APIRequestValidator> validators
  )
  {
    if (attribute.isAssociation()) {
      parsers.put(attribute,
                  APIRequestSelectionParserFactory.asCollectionOf(attribute.getName(),
                                                                  attribute.getJavaType(),
                                                                  Filter.expression(
                                                                    ":this IN :context." + attribute.getName())
                  )
      );
    }
  }

  private static <Entity> void generateSingleAttributeConfiguration (
    @NonNull final Attribute<? super Entity, ?> attribute,
    @NonNull final Map<Attribute<? super Entity, ?>, APIRequestParser<Operator>> parsers,
    @NonNull final Map<Attribute<? super Entity, ?>, APIRequestValidator> validators
  )
  {
    if (attribute.isAssociation()) {
      parsers.put(attribute, APIRequestSelectionParserFactory.asJoinWith(attribute.getName(), attribute.getJavaType()));
    } else {
      @NonNull final Optional<APIRequestSelectionParser> filter = rawAttributeToSelection(attribute);

      if (filter.isPresent()) {
        parsers.put(attribute, filter.get());
        validators.put(attribute, filter.get());
      }
    }
  }

  private static <Entity> @NonNull Optional<APIRequestSelectionParser> rawAttributeToSelection (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    @NonNull final Class<?>                   attributeType = attribute.getJavaType();
    @Nullable final APIRequestSelectionParser result;

    if (Double.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asDouble(attribute.getName());
    } else if (Float.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asFloat(attribute.getName());
    } else if (Long.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asLong(attribute.getName());
    } else if (Integer.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asInteger(attribute.getName());
    } else if (Short.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asShort(attribute.getName());
    } else if (Byte.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asByte(attribute.getName());
    } else if (Boolean.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asBoolean(attribute.getName());
    } else if (ZonedDateTime.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asDateTime(attribute.getName());
    } else if (CharSequence.class.isAssignableFrom(attributeType)) {
      result = APIRequestSelectionParserFactory.asString(attribute.getName());
    } else {
      result = null;
    }

    return Optional.ofNullable(result);
  }

  private EntityBasedSelectionConfiguration (
    @NonNull final Class<Entity> entity,
    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestParser<Operator>> parsers,
    @NonNull final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestValidator> validators
  )
  {
    _entity = entity;
    _parsers = parsers;
    _validators = validators;
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    return APIRequestValidator.composse(_validators.values().toArray(new APIRequestValidator[0]));
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    return APIRequestParser.compose(_parsers.values().toArray((APIRequestParser<Operator>[]) new APIRequestParser[0]))
             .map(Composition::of);
  }

  public @NonNull APIRequestValidator getAttributeValidator (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    return _validators.get(attribute);
  }

  public @NonNull APIRequestParser<Operator> getAttributeOperator (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    return _parsers.get(attribute);
  }

  public @NonNull Class<Entity> getEntity () {
    return _entity;
  }
}
