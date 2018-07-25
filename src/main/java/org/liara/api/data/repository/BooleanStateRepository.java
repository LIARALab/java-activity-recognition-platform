package org.liara.api.data.repository;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.springframework.lang.NonNull;

public interface BooleanStateRepository extends TimeSeriesRepository<BooleanState>
{
  public default Optional<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensors,
    final boolean value
  ) {
    return findPreviousWithValue(created.getEmittionDate(), Collections.singletonList(inputSensors), value);
  }
  
  public default Optional<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensors,
    final boolean value
  ) {
    return findPreviousWithValue(date, Collections.singletonList(inputSensors), value);
  }
  
  public default Optional<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value
  ) {
    return findPreviousWithValue(created.getEmittionDate(), inputSensors, value);
  }

  public default Optional<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime emittionDate, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value
  ) {
    final List<BooleanState> result = findPreviousWithValue(emittionDate, inputSensors, value, 1);
    
    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  public default List<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensor,
    final boolean value,
    final int count
  ) {
    return findPreviousWithValue(
      created.getEmittionDate(), 
      Collections.singletonList(inputSensor), 
      value, 
      count
    );
  }
  
  public default List<BooleanState> findPreviousWithValue (
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

  public default List<BooleanState> findPreviousWithValue (
    @NonNull final BooleanState created, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  ) {
    return findPreviousWithValue(
      created.getEmittionDate(), 
      inputSensors,
      value,
      count
    );
  }

  public List<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  );
  
  public default Optional<BooleanState> findNextWithValue (
    @NonNull final BooleanState created, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensors,
    final boolean value
  ) {
    return findNextWithValue(created.getEmittionDate(), Collections.singletonList(inputSensors), value);
  }
  
  public default Optional<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensors,
    final boolean value
  ) {
    return findNextWithValue(date, Collections.singletonList(inputSensors), value);
  }
  
  public default Optional<BooleanState> findNextWithValue (
    @NonNull final BooleanState created, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value
  ) {
    return findNextWithValue(created.getEmittionDate(), inputSensors, value);
  }

  public default Optional<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime emittionDate, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value
  ) {
    final List<BooleanState> result = findNextWithValue(emittionDate, inputSensors, value, 1);
    
    if (result.size() <= 0) return Optional.empty();
    else return Optional.ofNullable(result.get(0));
  }

  public default List<BooleanState> findNextWithValue (
    @NonNull final BooleanState created, 
    @NonNull final ApplicationEntityReference<Sensor> inputSensor,
    final boolean value,
    final int count
  ) {
    return findNextWithValue(
      created.getEmittionDate(), 
      Collections.singletonList(inputSensor), 
      value, 
      count
    );
  }
  
  public default List<BooleanState> findNextWithValue (
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

  public default List<BooleanState> findNextWithValue (
    @NonNull final BooleanState created, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  ) {
    return findNextWithValue(
      created.getEmittionDate(), 
      inputSensors,
      value,
      count
    );
  }

  public List<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date, 
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  );

  public List<BooleanState> findAllWithValue (
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors, 
    final boolean value
  );
}
