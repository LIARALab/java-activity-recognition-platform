package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class EntityConfiguration<Entity>
  implements CollectionRequestConfiguration
{
  @NonNull
  private final ManagedType<Entity> _entity;

  public EntityConfiguration (@NonNull final ManagedType<Entity> entity) {
    _entity = entity;
  }

  /**
   * Return a request validator for the given attribute.
   *
   * @param attribute - An attribute.
   *
   * @return A validator for the given attribute.
   */
  public abstract @NonNull APIRequestValidator getValidator (
    @NonNull final Attribute<? super Entity, ?> attribute
  );

  /**
   * Return a request validator for the given attribute.
   *
   * @param attribute - An attribute.
   *
   * @return A validator for the given attribute.
   */
  public @NonNull APIRequestValidator getValidator (
    @NonNull final String attribute
  )
  {
    return getValidator(_entity.getAttribute(attribute));
  }

  /**
   * Return a request validator for the given set of attributes.
   *
   * @param attributes
   *
   * @return A request validator for the given set of attributes.
   */
  public @NonNull APIRequestValidator getValidator (
    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes
  )
  {
    @NonNull final List<@NonNull APIRequestValidator> validators = new ArrayList<>(attributes.size());

    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      validators.add(getValidator(attribute));
    }

    return APIRequestValidator.compose(validators);
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    @NonNull final List<@NonNull APIRequestValidator> validators = new ArrayList<>(_entity.getAttributes().size());

    for (@NonNull final Attribute<? super Entity, ?> attribute : _entity.getAttributes()) {
      validators.add(getValidator(attribute));
    }

    return APIRequestValidator.compose(validators);
  }

  /**
   * Return a request parser for the given attribute.
   *
   * @param attribute - An attribute.
   *
   * @return A parser for the given attribute.
   */
  public abstract @NonNull APIRequestParser<Operator> getParser (
    @NonNull final Attribute<? super Entity, ?> attribute
  );

  /**
   * Return a request parser for the given attribute.
   *
   * @param attribute - An attribute.
   *
   * @return A parser for the given attribute.
   */
  public @NonNull APIRequestParser<Operator> getParser (
    @NonNull final String attribute
  )
  {
    return getParser(_entity.getAttribute(attribute));
  }

  /**
   * Return a request parser for the given set of attributes.
   *
   * @param attributes
   *
   * @return A request parser for the given set of attributes.
   */
  public @NonNull APIRequestParser<Operator> getParser (
    @NonNull final Set<@NonNull Attribute<? super Entity, ?>> attributes
  )
  {
    @NonNull final List<@NonNull APIRequestParser<Operator>> parsers = new ArrayList<>(attributes.size());

    for (@NonNull final Attribute<? super Entity, ?> attribute : attributes) {
      parsers.add(getParser(attribute));
    }

    return APIRequestParser.compose(parsers).map(Composition::of);
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    @NonNull final List<@NonNull APIRequestParser<Operator>> parsers = new ArrayList<>(_entity.getAttributes().size());

    for (@NonNull final Attribute<? super Entity, ?> attribute : _entity.getAttributes()) {
      parsers.add(getParser(attribute));
    }

    return APIRequestParser.compose(parsers).map(Composition::of);
  }

  public @NonNull Class<Entity> getEntity () {
    return _entity.getJavaType();
  }

  public @NonNull ManagedType<Entity> getManagedType () {
    return _entity;
  }
}
