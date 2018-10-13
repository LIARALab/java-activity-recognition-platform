package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import java.util.Arrays;

public class CompoundRequestConfiguration<Entity>
  implements CollectionRequestConfiguration<Entity>
{
  @NonNull
  private final CollectionRequestConfiguration<Entity>[] _configurations;

  public CompoundRequestConfiguration (@NonNull final CollectionRequestConfiguration<Entity>... configuration) {
    _configurations = Arrays.copyOf(configuration, configuration.length);
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    @NonNull final APIRequestValidator[] validators = new APIRequestValidator[_configurations.length];

    for (int index = 0; index < _configurations.length; ++index) {
      validators[index] = _configurations[index].getValidator();
    }

    return APIRequestValidator.composse(validators);
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    @NonNull final APIRequestParser<Operator>[] parsers =
      (APIRequestParser<Operator>[]) new APIRequestParser[_configurations.length];

    for (int index = 0; index < _configurations.length; ++index) {
      parsers[index] = _configurations[index].getParser();
    }

    return APIRequestParser.compose(parsers).map(Composition::of);
  }
}
