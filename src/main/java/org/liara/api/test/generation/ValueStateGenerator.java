package org.liara.api.test.generation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.ValueState;

import java.util.function.Supplier;

public interface ValueStateGenerator<State extends ValueState>
  extends Supplier<State>
{
  @NonNull Class<State> getGeneratedClass ();
}
