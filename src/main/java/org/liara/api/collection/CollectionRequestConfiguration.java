package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.request.APIRequest;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.APIRequestValidator;

public interface CollectionRequestConfiguration<Entity>
{
  /**
   * Return configured request validator for this request type.
   *
   * @return A configured request validator for this request type.
   */
  @NonNull APIRequestValidator getValidator ();

  /**
   * Return configured request parser for this request type.
   *
   * @return A configured request parser for this request type.
   */
  @NonNull APIRequestParser<Operator> getParser ();

  /**
   * Parse a given API request in accordance with this configuration and return a collection operator.
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
