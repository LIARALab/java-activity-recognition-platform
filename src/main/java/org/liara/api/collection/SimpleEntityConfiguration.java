package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import java.util.HashMap;
import java.util.Map;

public class SimpleEntityConfiguration<Entity>
  extends EntityConfiguration<Entity>
{
  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestValidator> _validators;

  @NonNull
  private final Map<@NonNull Attribute<? super Entity, ?>, @NonNull APIRequestParser<Operator>> _parsers;

  public SimpleEntityConfiguration (@NonNull final ManagedType<Entity> entity) {
    super(entity);
    _validators = new HashMap<>();
    _parsers = new HashMap<>();
  }

  public void setValidator (
    @NonNull final Attribute<? super Entity, ?> attribute, @NonNull final APIRequestValidator validator
  )
  {
    _validators.put(attribute, validator);
  }

  public void setValidator (
    @NonNull final String attribute, @NonNull final APIRequestValidator validator
  )
  {
    setValidator(getManagedType().getAttribute(attribute), validator);
  }

  @Override
  public @NonNull APIRequestValidator getValidator (
    @NonNull Attribute<? super Entity, ?> attribute
  )
  {
    return _validators.get(attribute);
  }

  public void setParser (
    @NonNull final Attribute<? super Entity, ?> attribute, @NonNull final APIRequestParser<Operator> parser
  )
  {
    _parsers.put(attribute, parser);
  }

  public void setParser (
    @NonNull final String attribute, @NonNull final APIRequestParser<Operator> parser
  )
  {
    setParser(getManagedType().getAttribute(attribute), parser);
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser (
    @NonNull Attribute<? super Entity, ?> attribute
  )
  {
    return _parsers.get(attribute);
  }
}
