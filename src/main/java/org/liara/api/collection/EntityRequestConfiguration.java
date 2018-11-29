package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Identity;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestFieldParser;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestFieldValidator;
import org.liara.request.validator.APIRequestValidator;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import java.util.*;
import java.util.stream.Collectors;

public class EntityRequestConfiguration<Entity>
  implements FieldConfiguration,
             RequestConfiguration
{
  @NonNull
  private final ManagedType<Entity> _entity;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestFieldValidator> _validators;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestFieldParser<Operator>> _parsers;

  public EntityRequestConfiguration (@NonNull final ManagedType<Entity> entity) {
    _entity = entity;
    _validators = new HashMap<>();
    _parsers = new HashMap<>();
  }

  /**
   * Return a request validator for the given attribute.
   *
   * @param attribute - An attribute.
   *
   * @return A validator for the given attribute.
   */
  public @NonNull Optional<APIRequestFieldValidator> getFieldValidator (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    if (_validators.containsKey(attribute)) {
      return Optional.of(_validators.get(attribute));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Return a request validator for the given attribute.
   *
   * @param name - An attribute name.
   *
   * @return A validator for the given attribute.
   */
  @Override
  public @NonNull Optional<APIRequestFieldValidator> getFieldValidator (
    @NonNull final String name
  )
  {
    return getAttribute(name).flatMap(this::getFieldValidator);
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    @NonNull final List<@NonNull APIRequestValidator> validators = new ArrayList<>(_entity.getAttributes().size());

    for (@NonNull final Attribute<? super Entity, ?> attribute : _entity.getAttributes()) {
      getFieldValidator(attribute).map(validator -> APIRequestValidator.field(attribute.getName(), validator))
        .ifPresent(validators::add);
    }

    return APIRequestValidator.all(validators);
  }

  /**
   * Return a request parser for the given attribute.
   *
   * @param attribute - An attribute.
   *
   * @return A parser for the given attribute.
   */
  public @NonNull Optional<APIRequestFieldParser<Operator>> getFieldParser (
    @NonNull final Attribute<? super Entity, ?> attribute
  )
  {
    if (_parsers.containsKey(attribute)) {
      return Optional.of(_parsers.get(attribute));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Return a request parser for the given attribute.
   *
   * @param name - An attribute name.
   *
   * @return A parser for the given attribute.
   */
  @Override
  public @NonNull Optional<APIRequestFieldParser<Operator>> getFieldParser (
    @NonNull final String name
  )
  {
    return getAttribute(name).flatMap(this::getFieldParser);
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    @NonNull final List<@NonNull APIRequestParser<Operator>> parsers = new ArrayList<>(_entity.getAttributes().size());

    for (@NonNull final Attribute<? super Entity, ?> attribute : _entity.getAttributes()) {
      getFieldParser(attribute).map(parser -> APIRequestParser.field(attribute.getName(), parser)
                                                .mapNonNull(Composition::of)
                                                .orElse(Identity.INSTANCE)).ifPresent(parsers::add);
    }

    return APIRequestParser.all(parsers).mapNonNull(Composition::of).orElse(Identity.INSTANCE);
  }

  public @NonNull Optional<Attribute<? super Entity, ?>> getAttribute (@NonNull final String name) {
    try {
      return Optional.of(_entity.getAttribute(name));
    } catch (@NonNull final IllegalArgumentException exception) {
      return Optional.empty();
    }
  }

  public @NonNull Class<Entity> getEntity () {
    return _entity.getJavaType();
  }

  public @NonNull ManagedType<Entity> getManagedType () {
    return _entity;
  }

  @Override
  public @NonNull Set<@NonNull String> getFields () {
    return _parsers.keySet().stream().map(Attribute::getName).collect(Collectors.toSet());
  }

  @Override
  public boolean containsField (@NonNull final String name) {
    @NonNull final Attribute<? super Entity, ?> attribute = _entity.getAttribute(name);
    return _parsers.containsKey(attribute);
  }
}
