package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.ValueState;
import org.liara.collection.operator.cursoring.Cursor;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ValueStateRepository<Value>
  extends StateRepository<ValueState<Value>>
{
  default @NonNull Optional<ValueState<Value>> findPreviousWithValue (
    @NonNull final ValueState created,
    @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
    @NonNull final Value value
  ) {
    return findPreviousWithValue(created.getEmissionDate(), Collections.singletonList(inputSensor), value);
  }

  default @NonNull Optional<ValueState<Value>> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
    @NonNull final Value value
  ) {
    return findPreviousWithValue(date, Collections.singletonList(inputSensor), value);
  }

  default @NonNull Optional<ValueState<Value>> findPreviousWithValue (
    @NonNull final ValueState<Value> created,
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value
  ) {
    return findPreviousWithValue(created.getEmissionDate(), inputSensors, value);
  }

  default @NonNull Optional<ValueState<Value>> findPreviousWithValue (
    @NonNull final ZonedDateTime emittionDate,
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value
  ) {
    @NonNull final List<ValueState<Value>> result = findPreviousWithValue(emittionDate,
                                                                          inputSensors,
                                                                          value,
                                                                          Cursor.FIRST
    );
    
    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  default @NonNull List<@NonNull ValueState<Value>> findPreviousWithValue (
    @NonNull final ValueState<Value> created,
    @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findPreviousWithValue(
      created.getEmissionDate(),
      Collections.singletonList(inputSensor),
      value, cursor
    );
  }

  default @NonNull List<@NonNull ValueState<Value>> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findPreviousWithValue(
      date, Collections.singletonList(inputSensor),
      value, cursor
    );
  }

  default @NonNull List<@NonNull ValueState<Value>> findPreviousWithValue (
    @NonNull final ValueState<Value> created,
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findPreviousWithValue(
      created.getEmissionDate(),
      inputSensors,
      value, cursor
    );
  }

  @NonNull List<@NonNull ValueState<Value>> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  );

  default @NonNull Optional<ValueState<Value>> findNextWithValue (
    @NonNull final ValueState<Value> created,
    @NonNull final ApplicationEntityReference<? extends Sensor> inputSensors,
    @NonNull final Value value
  ) {
    return findNextWithValue(created.getEmissionDate(), Collections.singletonList(inputSensors), value);
  }

  default @NonNull Optional<ValueState<Value>> findNextWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final ApplicationEntityReference<? extends Sensor> inputSensors,
    @NonNull final Value value
  ) {
    return findNextWithValue(date, Collections.singletonList(inputSensors), value);
  }

  default @NonNull Optional<ValueState<Value>> findNextWithValue (
    @NonNull final ValueState<Value> created,
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value
  ) {
    return findNextWithValue(created.getEmissionDate(), inputSensors, value);
  }

  default @NonNull Optional<ValueState<Value>> findNextWithValue (
    @NonNull final ZonedDateTime emittionDate,
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value
  ) {
    @NonNull final List<@NonNull ValueState<Value>> result = findNextWithValue(
      emittionDate,
      inputSensors,
      value,
      Cursor.FIRST
    );
    
    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  default @NonNull List<@NonNull ValueState<Value>> findNextWithValue (
    @NonNull final ValueState<Value> created,
    @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findNextWithValue(
      created.getEmissionDate(),
      Collections.singletonList(inputSensor),
      value, cursor
    );
  }

  default @NonNull List<@NonNull ValueState<Value>> findNextWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findNextWithValue(
      date,
      Collections.singletonList(inputSensor), value, cursor
    );
  }

  default @NonNull List<@NonNull ValueState<Value>> findNextWithValue (
    @NonNull final ValueState<Value> created,
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    return findNextWithValue(
      created.getEmissionDate(),
      inputSensors,
      value, cursor
    );
  }

  @NonNull List<@NonNull ValueState<Value>> findNextWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  );

  default @NonNull List<@NonNull ValueState<Value>> findAllWithValue (
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors, @NonNull final Value value
  )
  {
    return findAllWithValue(inputSensors, value, Cursor.ALL);
  }

  @NonNull List<@NonNull ValueState<Value>> findAllWithValue (
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  );
}