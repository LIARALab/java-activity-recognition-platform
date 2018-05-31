package org.liara.api.utils;

import org.springframework.lang.NonNull;

import groovy.lang.Closure;

public final class Closures
{
  public static <X, Delegate> void callAs (
    @NonNull final Closure<X> closure,
    @NonNull final Delegate delegate
  ) {
    final Closure<X> hydrated = closure.rehydrate(
      delegate, closure.getOwner(), closure.getThisObject()
    );
        
    hydrated.setResolveStrategy(Closure.DELEGATE_ONLY);
    hydrated.call();
  }
}
