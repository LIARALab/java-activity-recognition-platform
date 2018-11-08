package org.liara.api.data.repository;

import groovy.lang.Range;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.collection.operator.cursoring.Cursor;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface StateRepository<TimeState extends State>
       extends ApplicationEntityRepository<TimeState>
{
  default @NonNull Optional<TimeState> findPrevious (@NonNull final TimeState state) {
    return findPrevious(state.getEmissionDate(), state.getSensorIdentifier());
  }

  default @NonNull List<@NonNull TimeState> findAllPrevious (@NonNull final TimeState state) {
    return findAllPrevious(state.getEmissionDate(), state.getSensorIdentifier());
  }

  default @NonNull List<@NonNull TimeState> findPrevious (
    @NonNull final TimeState state, @NonNull final Cursor cursor
  )
  {
    return findPrevious(state.getEmissionDate(), Collections.singletonList(state.getSensorIdentifier()), cursor
    );
  }

  default @NonNull Optional<TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    final List<TimeState> result = findPrevious(date, Collections.singletonList(sensor), Cursor.FIRST);
    
    return result.size() > 0 ? Optional.ofNullable(result.get(0)) 
                             : Optional.empty();
  }

  default @NonNull List<@NonNull TimeState> findAllPrevious (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  )
  {
    return findPrevious(date, Collections.singletonList(sensor), Cursor.ALL);
  }

  @NonNull List<@NonNull TimeState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final Collection<@NonNull ApplicationEntityReference<? extends Sensor>> sensors,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<TimeState> findNext (@NonNull final TimeState state) {
    return findNext(state.getEmissionDate(), state.getSensorIdentifier());
  }

  default @NonNull List<@NonNull TimeState> findNext (
    @NonNull final TimeState state, @NonNull final Cursor cursor
  ) {
    return findNext(state.getEmissionDate(), Collections.singletonList(state.getSensorIdentifier()), cursor);
  }

  default @NonNull List<@NonNull TimeState> findAllNext (@NonNull final TimeState state) {
    return findNext(state.getEmissionDate(), Collections.singletonList(state.getSensorIdentifier()), Cursor.ALL);
  }

  default @NonNull Optional<TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    final List<TimeState> results = findNext(date, Collections.singletonList(sensor), Cursor.FIRST);
    
    return results.size() > 0 ? Optional.of(results.get(0))
                              : Optional.empty();
  }

  @NonNull List<@NonNull TimeState> findNext (
    @NonNull final ZonedDateTime date,
    @NonNull final Collection<@NonNull ApplicationEntityReference<? extends Sensor>> sensors,
    @NonNull final Cursor count
  );

  default @NonNull List<@NonNull TimeState> find (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor, @NonNull final Cursor cursor
  )
  {
    return find(Collections.singletonList(sensor), cursor);
  }

  @NonNull List<@NonNull TimeState> find (
    @NonNull final Collection<ApplicationEntityReference<? extends Sensor>> sensors, @NonNull final Cursor cursor
  );

  default @NonNull List<@NonNull TimeState> getAt (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final Range<Integer> range
  ) {
    return find(sensor, new Cursor(range.getFrom(), range.getTo() - range.getFrom()));
  }

  default @NonNull Optional<TimeState> findAt (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor, @NonNegative final int index
  ) {
    @NonNull final List<@NonNull TimeState> result = find(sensor, Cursor.FIRST.setOffset(index));

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  default @NonNull TimeState getAt (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor, @NonNegative final int index
  ) {
    return findAt(sensor, index).get();
  }

  default @NonNull Optional<TimeState> findFirst (@NonNull final ApplicationEntityReference<? extends Sensor> sensor) {
    return findAt(sensor, 0);
  }

  @NonNull Optional<TimeState> findLast (
    @NonNull final Collection<@NonNull ApplicationEntityReference<? extends Sensor>> sensors
  );
}
