package org.liara.api.data.repository;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface BooleanStateRepository extends TimeSeriesRepository<BooleanState>
{
  default Optional<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensors,
    final boolean value
  ) {
    return findPreviousWithValue(
      created,
      Collections.singletonList(inputSensors),
      value
    );
  }

  default Optional<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value
  ) {
    final List<BooleanState> states = findPreviousWithValue(
      created,
      inputSensors,
      value,
      1
    );
    return states.size() > 0 ? Optional.of(states.get(0)) : Optional.empty();
  }

  default List<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created,
    @NonNull final ApplicationEntityReference<Sensor> inputSensor,
    final boolean value,
    final int count
  )
  {
    return findPreviousWithValue(
      created,
      Collections.singletonList(inputSensor),
      value,
      count
    );
  }

  List<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors, final boolean value, final int count
  );

  default Optional<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<Sensor> inputSensors,
    final boolean value
  ) {
    return findPreviousWithValue(
      date,
      Collections.singletonList(inputSensors),
      value
    );
  }

  default Optional<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime emittionDate, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value
  ) {
    final List<BooleanState> result = findPreviousWithValue(emittionDate, inputSensors, value, 1);
    
    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  default List<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final ApplicationEntityReference<Sensor> inputSensor,
    final boolean value,
    final int count
  ) {
    return findPreviousWithValue(
      date,
      Collections.singletonList(inputSensor),
      value,
      count
    );
  }

  List<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  );

  default Optional<BooleanState> findNextWithValue (
    @NonNull final BooleanState created,
    @NonNull final ApplicationEntityReference<Sensor> inputSensors,
    final boolean value
  ) {
    return findNextWithValue(
      created,
      Collections.singletonList(inputSensors),
      value
    );
  }

  default Optional<BooleanState> findNextWithValue (
    @NonNull final BooleanState created,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors, final boolean value
  )
  {
    final List<BooleanState> result = findNextWithValue(
      created,
      inputSensors,
      value,
      1
    );
    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }

  default List<BooleanState> findNextWithValue (
    @NonNull final BooleanState created,
    @NonNull final ApplicationEntityReference<Sensor> inputSensor,
    final boolean value,
    final int count
  ) {
    return findNextWithValue(
      created,
      Collections.singletonList(inputSensor),
      value,
      count
    );
  }

  List<BooleanState> findNextWithValue (
    @NonNull final BooleanState created,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  );

  default Optional<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensors,
    final boolean value
  ) {
    return findNextWithValue(date, Collections.singletonList(inputSensors), value);
  }

  default Optional<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime emittionDate, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value
  ) {
    final List<BooleanState> result = findNextWithValue(emittionDate, inputSensors, value, 1);
    
    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  default List<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensor,
    final boolean value,
    final int count
  ) {
    return findNextWithValue(
      date, 
      Collections.singletonList(inputSensor), 
      value, 
      count
    );
  }

  List<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  );

  List<BooleanState> findAllWithValue (
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors, 
    final boolean value
  );
}
