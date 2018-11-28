package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A composition of request configuration.
 */
public class RequestConfigurationComposition
  implements CollectionRequestConfiguration
{
  @NonNull
  private final List<@NonNull CollectionRequestConfiguration> _configurations;

  /**
   * Create a composition of configurations.
   *
   * @param configuration
   */
  public RequestConfigurationComposition (
    @NonNull final CollectionRequestConfiguration... configuration
  )
  {
    _configurations = new ArrayList<>(Arrays.asList(configuration));
  }

  /**
   * Create a composition of configurations.
   *
   * @param configurations
   */
  public RequestConfigurationComposition (
    @NonNull final Collection<CollectionRequestConfiguration> configurations
  )
  {
    _configurations = new ArrayList<>(configurations);
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    @NonNull final APIRequestValidator[] validators = new APIRequestValidator[_configurations.size()];

    for (int index = 0; index < _configurations.size(); ++index) {
      validators[index] = _configurations.get(index).getValidator();
    }

    return APIRequestValidator.compose(validators);
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    @NonNull final List<@NonNull APIRequestParser<Operator>> parsers = new ArrayList<>(_configurations.size());

    for (int index = 0; index < _configurations.size(); ++index) {
      parsers.add(_configurations.get(index).getParser());
    }

    return APIRequestParser.compose(parsers).map(Composition::of);
  }
}
