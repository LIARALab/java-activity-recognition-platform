package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.ValueState;
import org.liara.collection.operator.cursoring.Cursor;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ValueStateRepository<Value, Wrapper extends ValueState>
  extends StateRepository<Wrapper>
{
  default @NonNull Optional<Wrapper> findPreviousWithValue (
    @NonNull final ValueState created, @NonNull final Long inputSensorIdentifier,
    @NonNull final Value value
  ) {
    return findPreviousWithValue(
      Objects.requireNonNull(created.getEmissionDate()),
      Collections.singletonList(inputSensorIdentifier),
      value
    );
  }

  default @NonNull Optional<Wrapper> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final Long inputSensorIdentifier,
    @NonNull final Value value
  ) {
    return findPreviousWithValue(
      date,
      Collections.singletonList(inputSensorIdentifier),
      value
    );
  }

  default @NonNull Optional<Wrapper> findPreviousWithValue (
    @NonNull final Wrapper created, @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value
  ) {
    return findPreviousWithValue(
      Objects.requireNonNull(created.getEmissionDate()),
      inputSensorIdentifiers,
      value
    );
  }

  default @NonNull Optional<Wrapper> findPreviousWithValue (
    @NonNull final ZonedDateTime emissionDate, @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value
  ) {
    @NonNull final List<Wrapper> result = findPreviousWithValue(
      emissionDate,
      inputSensorIdentifiers,
      value,
      Cursor.FIRST
    );

    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  default @NonNull List<@NonNull Wrapper> findPreviousWithValue (
    @NonNull final Wrapper created, @NonNull final Long inputSensorIdentifier,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findPreviousWithValue(Objects.requireNonNull(created.getEmissionDate()),
                                 Collections.singletonList(inputSensorIdentifier),
                                 value, cursor
    );
  }

  default @NonNull List<@NonNull Wrapper> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final Long inputSensorIdentifier,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findPreviousWithValue(
      date,
      Collections.singletonList(inputSensorIdentifier),
      value,
      cursor
    );
  }

  default @NonNull List<@NonNull Wrapper> findPreviousWithValue (
    @NonNull final Wrapper created, @NonNull final List<@NonNull Long> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findPreviousWithValue(Objects.requireNonNull(created.getEmissionDate()),
                                 inputSensors,
                                 value, cursor
    );
  }

  @NonNull List<@NonNull Wrapper> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<Wrapper> findNextWithValue (
    @NonNull final Wrapper created, @NonNull final Long inputSensorIdentifiers,
    @NonNull final Value value
  ) {
    return findNextWithValue(
      Objects.requireNonNull(created.getEmissionDate()),
      Collections.singletonList(inputSensorIdentifiers),
      value
    );
  }

  default @NonNull Optional<Wrapper> findNextWithValue (
    @NonNull final ZonedDateTime date, @NonNull final Long inputSensorIdentifier,
    @NonNull final Value value
  ) {
    return findNextWithValue(
      date,
      Collections.singletonList(inputSensorIdentifier),
      value
    );
  }

  default @NonNull Optional<Wrapper> findNextWithValue (
    @NonNull final Wrapper created, @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value
  ) {
    return findNextWithValue(
      Objects.requireNonNull(created.getEmissionDate()),
      inputSensorIdentifiers,
      value
    );
  }

  default @NonNull Optional<Wrapper> findNextWithValue (
    @NonNull final ZonedDateTime emissionDate, @NonNull final List<Long> inputSensorIdentifier,
    @NonNull final Value value
  ) {
    @NonNull final List<@NonNull Wrapper> result = findNextWithValue(
      emissionDate,
      inputSensorIdentifier,
      value,
      Cursor.FIRST
    );

    return result.size() <= 0 ? Optional.empty() : Optional.of(result.get(0));
  }

  default @NonNull List<@NonNull Wrapper> findNextWithValue (
    @NonNull final Wrapper created, @NonNull final Long inputSensorIdentifier,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findNextWithValue(Objects.requireNonNull(created.getEmissionDate()),
                             Collections.singletonList(inputSensorIdentifier),
                             value, cursor
    );
  }

  default @NonNull List<@NonNull Wrapper> findNextWithValue (
    @NonNull final ZonedDateTime date, @NonNull final Long inputSensorIdentifier,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findNextWithValue(
      date,
      Collections.singletonList(inputSensorIdentifier),
      value,
      cursor
    );
  }

  default @NonNull List<@NonNull Wrapper> findNextWithValue (
    @NonNull final Wrapper created, @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findNextWithValue(Objects.requireNonNull(created.getEmissionDate()),
                             inputSensorIdentifiers,
                             value, cursor
    );
  }

  @NonNull List<@NonNull Wrapper> findNextWithValue (
    @NonNull final ZonedDateTime date, @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  );

  default @NonNull List<@NonNull Wrapper> findAllWithValue (
    @NonNull final List<@NonNull Long> inputSensorIdentifiers, @NonNull final Value value
  )
  {
    return findAllWithValue(
      inputSensorIdentifiers,
      value,
      Cursor.ALL
    );
  }

  @NonNull List<@NonNull Wrapper> findAllWithValue (
    @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  );
}
