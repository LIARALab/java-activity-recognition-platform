package org.liara.api.data.series;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.LongValueState;
import org.liara.api.data.repository.LongValueStateRepository;
import org.liara.collection.operator.cursoring.Cursor;

import java.time.ZonedDateTime;
import java.util.*;

public class LongSeries
  implements Series<LongValueState>
{
  @NonNull
  @NonNegative
  private final Long _sensor;

  @NonNull
  private final LongValueStateRepository _longValues;

  @NonNull
  private final Map<@NonNull ZonedDateTime, @NonNull Optional<LongValueState>> _previousCache;

  @NonNull
  private final Map<@NonNull ZonedDateTime, @NonNull Optional<LongValueState>> _nextCache;

  public LongSeries (@NonNull final LongSeriesBuilder builder) {
    _sensor = Objects.requireNonNull(builder.getSensor());
    _longValues = Objects.requireNonNull(builder.getLongValues());
    _previousCache = new HashMap<>();
    _nextCache = new HashMap<>();
  }

  public LongSeries (@NonNull final LongSeries toCopy) {
    _sensor = toCopy.getSensor();
    _longValues = toCopy._longValues;
    _previousCache = new HashMap<>();
    _nextCache = new HashMap<>();
  }

  @Override
  public @NonNull List<LongValueState> get () {
    return _longValues.find(_sensor, Cursor.ALL);
  }

  @Override
  public @NonNull Optional<LongValueState> getPrevious (@NonNull final ZonedDateTime time) {
    if (_previousCache.containsKey(time)) {
      return _previousCache.get(time);
    } else {
      @NonNull final Optional<LongValueState> state = _longValues.findPrevious(time, _sensor);
      _previousCache.put(time, state);
      return state;
    }
  }

  @Override
  public @NonNull List<@NonNull LongValueState> getPrevious (
    @NonNull final ZonedDateTime time,
    @NonNegative final int count
  ) {
    return _longValues.findPrevious(
      time,
      Collections.singletonList(_sensor),
      new Cursor(count)
    );
  }

  @Override
  public @NonNull Optional<LongValueState> getNext (@NonNull final ZonedDateTime time) {
    if (_nextCache.containsKey(time)) {
      return _nextCache.get(time);
    } else {
      @NonNull final Optional<LongValueState> state = _longValues.findNext(time, _sensor);
      _nextCache.put(time, state);
      return state;
    }
  }

  @Override
  public @NonNull List<@NonNull LongValueState> getNext (
    @NonNull final ZonedDateTime time,
    @NonNegative final int count
  ) {
    return _longValues.findNext(
      time,
      Collections.singletonList(_sensor),
      new Cursor(count)
    );
  }

  @Override
  public void forget () {
    _previousCache.clear();
    _nextCache.clear();
  }

  @Override
  public @NonNull @NonNegative Long getSensor () {
    return _sensor;
  }
}
