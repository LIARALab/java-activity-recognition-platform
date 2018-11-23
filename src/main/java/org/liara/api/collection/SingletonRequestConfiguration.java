package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

public class SingletonRequestConfiguration
  implements CollectionRequestConfiguration
{
  @NonNull
  private final APIRequestValidator _validator;

  @NonNull
  private final APIRequestParser<Operator> _parser;

  public SingletonRequestConfiguration (
    @NonNull final APIRequestValidator validator, @NonNull final APIRequestParser<Operator> parser
  )
  {
    _validator = validator;
    _parser = parser;
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    return _validator;
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    return _parser;
  }
}
