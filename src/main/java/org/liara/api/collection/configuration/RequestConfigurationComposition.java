package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import java.util.*;
import java.util.stream.Collectors;

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

  public RequestConfigurationComposition (
    @NonNull final RequestConfigurationComposition toCopy
  )
  {
    _configurations = new ArrayList<>(toCopy.getConfigurations());
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    return APIRequestValidator.all(_configurations.stream()
                                     .map(RequestConfiguration::getValidator)
                                     .collect(Collectors.toList()));
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    return APIRequestParser.all(_configurations.stream()
                                  .map(RequestConfiguration::getParser)
                                  .collect(Collectors.toList())).mapNonNull(Composition::of);
  }

  public @NonNull List<@NonNull RequestConfiguration> getConfigurations () {
    return Collections.unmodifiableList(_configurations);
  }

  @Override
  public int hashCode () {
    return Objects.hash(_configurations);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof RequestConfigurationComposition) {
      @NonNull final RequestConfigurationComposition otherComposition = (RequestConfigurationComposition) other;
      return Objects.equals(_configurations, otherComposition.getConfigurations());
    }

    return false;
  }
}
