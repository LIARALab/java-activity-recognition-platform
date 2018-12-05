package org.liara.api.collection.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public interface MapConfiguration<Leaf>
{
  default @NonNull Optional<Leaf> getFieldConfiguration (@NonNull final RequestPath path) {
    @Nullable MapConfiguration<Leaf> current = this;

    for (int index = 0; index < path.size() - 1; ++index) {
      @NonNull final String parameter = path.getParameter(index);
      if (current != null && current.contains(parameter)) {
        current = getRequestConfiguration(parameter).orElse(null);
      } else {
        return Optional.empty();
      }
    }

    return current != null ? current.getFieldConfiguration(path.last()) : Optional.empty();
  }

  @NonNull Optional<Leaf> getFieldConfiguration (
    @NonNull final String parameter
  );

  default @NonNull Optional<MapConfiguration<Leaf>> getRequestConfiguration (
    @NonNull final RequestPath path
  )
  {
    @Nullable MapConfiguration<Leaf> current = this;

    for (int index = 0; index < path.size(); ++index) {
      @NonNull final String parameter = path.getParameter(index);
      if (current != null && current.contains(parameter)) {
        current = getRequestConfiguration(parameter).orElse(null);
      } else {
        return Optional.empty();
      }
    }

    return Optional.ofNullable(current);
  }

  @NonNull Optional<MapConfiguration<Leaf>> getRequestConfiguration (
    @NonNull final String parameter
  );

  default @NonNull Set<@NonNull RequestPath> getDeepParameters () {
    @NonNull final Set<@NonNull RequestPath>                paths          = new HashSet<>();
    @NonNull final List<@NonNull Iterator<@NonNull String>> iteratorStack  = new ArrayList<>();
    @NonNull final List<@NonNull String>                    parameterStack = new ArrayList<>();

    getFieldParameters().stream().map(RequestPath::new).forEach(paths::add);
    iteratorStack.add(getRequestParameters().iterator());

    while (iteratorStack.size() > 0) {
      @NonNull final Iterator<@NonNull String> currentIterator = iteratorStack.get(iteratorStack.size() - 1);

      if (currentIterator.hasNext()) {
        @NonNull final String                 parameter        = currentIterator.next();
        @NonNull final MapConfiguration<Leaf> subConfiguration = getRequestConfiguration(parameter).orElseThrow();

        parameterStack.add(parameter);

        @NonNull final RequestPath basePath = new RequestPath(parameterStack);

        for (@NonNull final String fieldParameter : subConfiguration.getFieldParameters()) {
          paths.add(basePath.concat(fieldParameter));
        }

        iteratorStack.add(subConfiguration.getRequestParameters().iterator());
      } else {
        iteratorStack.remove(iteratorStack.size() - 1);
        if (iteratorStack.size() > 0) parameterStack.remove(parameterStack.size() - 1);
      }
    }

    return paths;
  }

  @NonNull Set<@NonNull String> getFieldParameters ();

  @NonNull Set<@NonNull String> getRequestParameters ();

  default boolean contains (@NonNull final RequestPath path) {
    @Nullable MapConfiguration<Leaf> current = this;

    for (int index = 0; index < path.size(); ++index) {
      @NonNull final String parameter = path.getParameter(index);
      if (current != null && current.contains(parameter)) {
        current = getRequestConfiguration(parameter).orElse(null);
      } else {
        return false;
      }
    }

    return true;
  }

  boolean contains (@NonNull final String parameter);
}
