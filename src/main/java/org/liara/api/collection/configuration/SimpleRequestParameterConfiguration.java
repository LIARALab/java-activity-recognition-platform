package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestFieldParser;
import org.liara.request.validator.APIRequestFieldValidator;

import java.util.Objects;

public class SimpleRequestParameterConfiguration
  implements RequestParameterConfiguration
{
  @NonNull
  private final APIRequestFieldParser<Operator> _parser;

  @NonNull
  private final APIRequestFieldValidator _validator;

  public SimpleRequestParameterConfiguration (
    @NonNull final APIRequestFieldParser<Operator> parser, @NonNull final APIRequestFieldValidator validator
  )
  {
    _parser = parser;
    _validator = validator;
  }

  public SimpleRequestParameterConfiguration (@NonNull final SimpleRequestParameterConfiguration toCopy) {
    _parser = toCopy.getParser();
    _validator = toCopy.getValidator();
  }

  @Override
  public @NonNull APIRequestFieldParser<Operator> getParser () {
    return _parser;
  }

  @Override
  public @NonNull APIRequestFieldValidator getValidator () {
    return _validator;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_parser, _validator);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof SimpleRequestParameterConfiguration) {
      @NonNull final SimpleRequestParameterConfiguration otherConfiguration =
        (SimpleRequestParameterConfiguration) other;

      return Objects.equals(_parser, otherConfiguration.getParser()) && Objects.equals(
        _validator,
        otherConfiguration.getValidator()
      );
    }

    return false;
  }
}
