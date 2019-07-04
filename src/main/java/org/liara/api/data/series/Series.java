package org.liara.api.data.series;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface Series<Value extends State>
{
  @NonNull @NonNegative Long getSensor ();

  @NonNull List<@NonNull Value> get ();

  @NonNull Optional<Value> getPrevious (@NonNull final ZonedDateTime time);

  @NonNull List<@NonNull Value> getPrevious (
    @NonNull final ZonedDateTime time,
    @NonNegative final int count
  );

  @NonNull Optional<Value> getNext (@NonNull final ZonedDateTime time);

  @NonNull List<@NonNull Value> getNext (
    @NonNull final ZonedDateTime time,
    @NonNegative final int count
  );

  void forget ();
}
