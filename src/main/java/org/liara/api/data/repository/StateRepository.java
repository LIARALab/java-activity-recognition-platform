package org.liara.api.data.repository;

import groovy.lang.Range;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;
import org.liara.collection.operator.cursoring.Cursor;

import java.time.ZonedDateTime;
import java.util.*;

public interface StateRepository<TimeState extends State>
       extends ApplicationEntityRepository<TimeState>
{
  default @NonNull Optional<TimeState> findPrevious (@NonNull final TimeState state) {
    return findPrevious(
      Objects.requireNonNull(state.getEmissionDate()),
      Objects.requireNonNull(state.getSensorIdentifier())
    );
  }

  default @NonNull List<@NonNull TimeState> findAllPrevious (@NonNull final TimeState state) {
    return findAllPrevious(
      Objects.requireNonNull(state.getEmissionDate()),
      Objects.requireNonNull(state.getSensorIdentifier())
    );
  }

  default @NonNull List<@NonNull TimeState> findPrevious (
    @NonNull final TimeState state, @NonNull final Cursor cursor
  )
  {
    return findPrevious(
      Objects.requireNonNull(state.getEmissionDate()),
      Collections.singletonList(state.getSensorIdentifier()),
      cursor
    );
  }

  default @NonNull Optional<TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final Long sensorIdentifier
  ) {
    @NonNull final List<@NonNull TimeState> result = findPrevious(
      date,
      Collections.singletonList(sensorIdentifier),
      Cursor.FIRST
    );

    return result.size() > 0 ? Optional.of(result.get(0))
                             : Optional.empty();
  }

  default @NonNull List<@NonNull TimeState> findAllPrevious (
    @NonNull final ZonedDateTime date, @NonNull final Long sensorIdentifier
  )
  {
    return findPrevious(
      date,
      Collections.singletonList(sensorIdentifier),
      Cursor.ALL
    );
  }

  @NonNull List<@NonNull TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final Collection<@NonNull Long> sensors,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<TimeState> findNext (@NonNull final TimeState state) {
    return findNext(
      Objects.requireNonNull(state.getEmissionDate()),
      Objects.requireNonNull(state.getSensorIdentifier())
    );
  }

  default @NonNull List<@NonNull TimeState> findNext (
    @NonNull final TimeState state, @NonNull final Cursor cursor
  ) {
    return findNext(
      Objects.requireNonNull(state.getEmissionDate()),
      Collections.singletonList(state.getSensorIdentifier()),
      cursor
    );
  }

  default @NonNull List<@NonNull TimeState> findAllNext (@NonNull final TimeState state) {
    return findNext(
      Objects.requireNonNull(state.getEmissionDate()),
      Collections.singletonList(state.getSensorIdentifier()),
      Cursor.ALL
    );
  }

  default @NonNull Optional<TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final Long sensorIdentifier
  ) {
    final List<TimeState> results = findNext(
      date,
      Collections.singletonList(sensorIdentifier),
      Cursor.FIRST
    );
    
    return results.size() > 0 ? Optional.of(results.get(0))
                              : Optional.empty();
  }

  @NonNull List<@NonNull TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor count
  );

  default @NonNull List<@NonNull TimeState> find (
    @NonNull final Long sensorIdentifier, @NonNull final Cursor cursor
  )
  {
    return find(
      Collections.singletonList(sensorIdentifier),
      cursor
    );
  }

  @NonNull List<@NonNull TimeState> find (
    @NonNull final Collection<Long> sensorIdentifiers, @NonNull final Cursor cursor
  );

  default @NonNull List<@NonNull TimeState> getAt (
    @NonNull final Long sensorIdentifier,
    @NonNull final Range<Integer> range
  ) {
    return find(
      sensorIdentifier,
      new Cursor(
        range.getFrom(),
        range.getTo() - range.getFrom()
      )
    );
  }

  default @NonNull Optional<TimeState> findAt (
    @NonNull final Long sensorIdentifier, @NonNegative final int index
  ) {
    @NonNull final List<@NonNull TimeState> result = find(
      sensorIdentifier,
      Cursor.FIRST.setOffset(index)
    );

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  default @NonNull TimeState getAt (
    @NonNull final Long sensorIdentifier, @NonNegative final int index
  ) {
    return findAt(
      sensorIdentifier,
      index
    ).orElseThrow();
  }

  default @NonNull Optional<TimeState> findFirst (
    @NonNull final Long sensorIdentifier
  )
  {
    return findAt(
      sensorIdentifier,
      0
    );
  }

  @NonNull Optional<TimeState> findLast (
    @NonNull final Collection<@NonNull Long> sensorIdentifiers
  );
}
