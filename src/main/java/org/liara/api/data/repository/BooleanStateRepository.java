package org.liara.api.data.repository;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.BooleanState;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface BooleanStateRepository
  extends StateRepository<BooleanState>
{
  default Optional<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, @NonNull final ApplicationEntityReference<? extends Sensor> inputSensors,
    final boolean value
  ) {
    return findPreviousWithValue(created.getEmissionDate(), Collections.singletonList(inputSensors), value);
  }

  default Optional<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> inputSensors,
    final boolean value
  ) {
    return findPreviousWithValue(date, Collections.singletonList(inputSensors), value);
  }

  default Optional<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value
  ) {
    return findPreviousWithValue(created.getEmissionDate(), inputSensors, value);
  }

  default Optional<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime emittionDate,
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value
  ) {
    final List<BooleanState> result = findPreviousWithValue(emittionDate, inputSensors, value, 1);
    
    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  default List<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
    final boolean value,
    final int count
  ) {
    return findPreviousWithValue(
      created.getEmissionDate(),
      Collections.singletonList(inputSensor),
      value,
      count
    );
  }

  default List<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
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

  default List<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value,
    final int count
  ) {
    return findPreviousWithValue(
      created.getEmissionDate(),
      inputSensors,
      value,
      count
    );
  }

  List<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value,
    final int count
  );

  default Optional<BooleanState> findNextWithValue (
    @NonNull final BooleanState created, @NonNull final ApplicationEntityReference<? extends Sensor> inputSensors,
    final boolean value
  ) {
    return findNextWithValue(created.getEmissionDate(), Collections.singletonList(inputSensors), value);
  }

  default Optional<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> inputSensors,
    final boolean value
  ) {
    return findNextWithValue(date, Collections.singletonList(inputSensors), value);
  }

  default Optional<BooleanState> findNextWithValue (
    @NonNull final BooleanState created, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value
  ) {
    return findNextWithValue(created.getEmissionDate(), inputSensors, value);
  }

  default Optional<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime emittionDate,
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value
  ) {
    final List<BooleanState> result = findNextWithValue(emittionDate, inputSensors, value, 1);
    
    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  default List<BooleanState> findNextWithValue (
    @NonNull final BooleanState created, @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
    final boolean value,
    final int count
  ) {
    return findNextWithValue(
      created.getEmissionDate(),
      Collections.singletonList(inputSensor),
      value,
      count
    );
  }

  default List<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> inputSensor,
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

  default List<BooleanState> findNextWithValue (
    @NonNull final BooleanState created, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value,
    final int count
  ) {
    return findNextWithValue(
      created.getEmissionDate(),
      inputSensors,
      value,
      count
    );
  }

  List<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value,
    final int count
  );

  List<BooleanState> findAllWithValue (
    @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final boolean value
  );
}
