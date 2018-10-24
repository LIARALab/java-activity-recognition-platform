package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CloneMemory
{
  @NonNull
  private final Map<@NonNull Object, @NonNull Object> _clones;

  public CloneMemory () {
    _clones = new HashMap<>();
  }

  public CloneMemory (@NonNull final CloneMemory toCopy) {
    _clones = new HashMap<>(toCopy.getClones());
  }

  public <T extends DeeplyCloneable> @NonNull T clone (@NonNull final T cloneable)
  {
    try {
      if (!_clones.containsKey(cloneable)) {
        @NonNull final Object clone = cloneable.clone(this);
        _clones.put(cloneable, clone);
      }

      return (T) _clones.get(cloneable);
    } catch (@NonNull final CloneNotSupportedException e) {
      throw new Error("Trying to deeply clone an uncloneable instance.", e);
    }
  }

  public <T extends DeeplyCloneable> void register (@NonNull final T original, @NonNull final T clone) {
    if (!_clones.containsKey(original)) {
      _clones.put(original, clone);
    } else {
      throw new Error("Unable to register a clone of the original instance " + original.toString() +
                      " because a clone was already registered for this instance.");
    }

  }

  public @NonNull Map<@NonNull Object, @NonNull Object> getClones () {
    return Collections.unmodifiableMap(_clones);
  }
}
