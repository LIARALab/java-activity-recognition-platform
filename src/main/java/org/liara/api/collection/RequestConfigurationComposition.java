package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Identity;
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
  implements RequestConfiguration
{
  @NonNull
  private final List<@NonNull RequestConfiguration> _configurations;

  /**
   * Create a composition of configurations.
   *
   * @param configurations Configurations to compose.
   */
  public RequestConfigurationComposition (
    @NonNull final RequestConfiguration... configurations
  )
  {
    _configurations = new ArrayList<>(Arrays.asList(configurations));
  }

  /**
   * Create a composition of configurations.
   *
   * @param configurations Configurations to compose.
   */
  public RequestConfigurationComposition (
    @NonNull final Collection<RequestConfiguration> configurations
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

    return APIRequestValidator.all(validators);
  }

  @Override
  public @NonNull APIRequestParser<@NonNull Operator> getParser () {
    @NonNull final List<@NonNull APIRequestParser<Operator>> parsers = new ArrayList<>(_configurations.size());

    for (int index = 0; index < _configurations.size(); ++index) {
      parsers.add(_configurations.get(index).getParser());
    }

    return APIRequestParser.all(parsers).mapNonNull(Composition::of).orElse(Identity.INSTANCE);
  }
}
