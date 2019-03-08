package org.liara.api.test.generation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.BooleanValueState;

public class BooleanStateGenerator
  implements ValueStateGenerator<BooleanValueState>
{
  @Override
  public @NonNull Class<BooleanValueState> getGeneratedClass () {
    return BooleanValueState.class;
  }

  @Override
  public BooleanValueState get () {
    return null;
  }
}
