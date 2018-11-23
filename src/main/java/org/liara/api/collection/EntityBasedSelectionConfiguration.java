package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import javax.persistence.metamodel.Attribute;
import java.util.Map;

public final class EntityBasedSelectionConfiguration<Entity>
  implements CollectionRequestConfiguration
{
  @NonNull
  private final Class<Entity> _entity;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestParser<Operator>> _parsers;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestValidator> _validators;

  EntityBasedSelectionConfiguration (
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
    return APIRequestValidator.compose(_validators.values().toArray(new APIRequestValidator[0]));
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
