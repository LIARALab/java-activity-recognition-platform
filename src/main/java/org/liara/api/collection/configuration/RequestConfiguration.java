package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.request.APIRequest;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.APIRequestValidator;

import java.util.Collection;

public interface RequestConfiguration
{
  static @NonNull RequestConfiguration all (@NonNull final Collection<@NonNull RequestConfiguration> configurations) {
    return new RequestConfigurationComposition(configurations);
  }

  static @NonNull RequestConfiguration all (@NonNull final RequestConfiguration... configurations) {
    return new RequestConfigurationComposition(configurations);
  }

  static @NonNull RequestConfiguration field (
    @NonNull final String field, @NonNull final RequestParameterConfiguration configuration
  )
  {
    return new RequestConfiguration()
    {
      @Override
      public @NonNull APIRequestValidator getValidator () {
        return APIRequestValidator.field(field, configuration.getValidator());
      }

      @Override
      public @NonNull APIRequestParser<@NonNull Operator> getParser () {
        return APIRequestParser.field(field, configuration.getParser()).mapNonNull(Composition::of);
      }
    };
  }

  /**
   * Return a validator for this request type.
   *
   * @return A validator for this request type.
   */
  @NonNull APIRequestValidator getValidator ();

  /**
   * Return a parser for this request type.
   *
   * @return A a parser for this request type.
   */
  @NonNull APIRequestParser<@NonNull Operator> getParser ();

  /**
   * Parse a given API request in accordance with this configuration and return an operator.
   *
   * @param request A request to parse.
   *
   * @return A collection operator.
   */
  default @NonNull Operator parse (@NonNull final APIRequest request) {
    return getParser().parse(request);
  }

  /**
   * Validate a given API request in accordance with this configuration and return a validation result.
   *
   * @param request A request to parse.
   *
   * @return A request validation.
   */
  default @NonNull APIRequestValidation validate (@NonNull final APIRequest request) {
    return getValidator().validate(request);
  }
}
