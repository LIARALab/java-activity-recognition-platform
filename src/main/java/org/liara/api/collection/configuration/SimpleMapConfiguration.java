package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.utils.Duplicator;

import java.util.*;

public class SimpleMapConfiguration<Leaf>
  implements MapConfiguration<Leaf>
{
  @NonNull
  private final Map<@NonNull String, @NonNull Leaf> _fieldConfigurations;

  @NonNull
  private final Map<@NonNull String, @NonNull MapConfiguration<Leaf>> _requestConfigurations;

  public SimpleMapConfiguration () {
    _fieldConfigurations = new HashMap<>();
    _requestConfigurations = new HashMap<>();
  }

  public SimpleMapConfiguration (@NonNull final SimpleMapConfiguration<Leaf> toCopy) {
    _fieldConfigurations = new HashMap<>();
    toCopy.getFieldConfigurations()
      .forEach((@NonNull final String key, @NonNull final Leaf value) -> _fieldConfigurations.put(
        key,
        Duplicator.duplicate(value)
      ));

    _requestConfigurations = new HashMap<>();
    _requestConfigurations.forEach((@NonNull final String key, @NonNull final MapConfiguration<Leaf> value) -> _requestConfigurations
                                                                                                                 .put(
                                                                                                                   key,
                                                                                                                   Duplicator
                                                                                                                     .duplicate(
                                                                                                                       value)
                                                                                                                 ));
  }

  public void set (@NonNull final String parameter, @NonNull final Leaf configuration) {
    if (_requestConfigurations.containsKey(parameter)) {
      throw new Error("Unable to configure as a field a parameter that was already configured as a sub-request.");
    }

    _fieldConfigurations.put(parameter, configuration);
  }

  public void set (@NonNull final String parameter, @NonNull final MapConfiguration<Leaf> configuration) {
    if (_fieldConfigurations.containsKey(parameter)) {
      throw new Error("Unable to configure as a sub-request a parameter that was already configured as a field.");
    }

    _requestConfigurations.put(parameter, configuration);
  }

  public void remove (@NonNull final String parameter) {
    _fieldConfigurations.remove(parameter);
    _requestConfigurations.remove(parameter);
  }

  private @NonNull Map<@NonNull String, @NonNull Leaf> getFieldConfigurations () {
    return Collections.unmodifiableMap(_fieldConfigurations);
  }

  public @NonNull Map<@NonNull String, @NonNull MapConfiguration<Leaf>> getRequestConfigurations () {
    return Collections.unmodifiableMap(_requestConfigurations);
  }

  @Override
  public @NonNull Optional<Leaf> getFieldConfiguration (@NonNull final String parameter) {
    return Optional.ofNullable(_fieldConfigurations.computeIfAbsent(parameter, x -> null));
  }

  @Override
  public @NonNull Optional<MapConfiguration<Leaf>> getRequestConfiguration (@NonNull final String parameter) {
    return Optional.ofNullable(_requestConfigurations.computeIfAbsent(parameter, x -> null));
  }

  @Override
  public @NonNull Set<@NonNull String> getFieldParameters () {
    return Collections.unmodifiableSet(_fieldConfigurations.keySet());
  }

  @Override
  public @NonNull Set<@NonNull String> getRequestParameters () {
    return Collections.unmodifiableSet(_requestConfigurations.keySet());
  }

  @Override
  public boolean contains (@NonNull final String parameter) {
    return _requestConfigurations.containsKey(parameter) || _fieldConfigurations.containsKey(parameter);
  }
}
