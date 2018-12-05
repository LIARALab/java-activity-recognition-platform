package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestFieldParser;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestFieldValidator;
import org.liara.request.validator.APIRequestValidator;

public interface RequestParameterConfiguration
  extends BindableConfiguration
{
  @NonNull APIRequestFieldParser<Operator> getParser ();

  @NonNull APIRequestFieldValidator getValidator ();

  default @NonNull RequestConfiguration bind (@NonNull final String parameter) {
    return new SimpleRequestConfiguration(
      APIRequestParser.field(parameter, getParser()).mapNonNull(Composition::of),
      APIRequestValidator.field(parameter, getValidator())
    );
  }
}
