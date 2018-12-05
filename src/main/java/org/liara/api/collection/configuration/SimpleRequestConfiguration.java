package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import java.util.Objects;

public class SimpleRequestConfiguration
  implements RequestConfiguration
{
  @NonNull
  private final APIRequestValidator _validator;

  @NonNull
  private final APIRequestParser<Operator> _parser;

  public SimpleRequestConfiguration (
    @NonNull final APIRequestParser<Operator> parser, @NonNull final APIRequestValidator validator
  )
  {
    _validator = validator;
    _parser = parser;
  }

  public SimpleRequestConfiguration (
    @NonNull final SimpleRequestConfiguration toCopy
  )
  {
    _validator = toCopy.getValidator();
    _parser = toCopy.getParser();
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    return _validator;
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    return _parser;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_validator, _parser);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof SimpleRequestConfiguration) {
      @NonNull final SimpleRequestConfiguration otherConfiguration = (SimpleRequestConfiguration) other;

      return Objects.equals(_validator, otherConfiguration.getValidator()) && Objects.equals(
        _parser,
        otherConfiguration.getParser()
      );
    }

    return false;
  }
}
