package org.liara.api.data.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

public interface TimeSeriesRepository<TimeState extends State>
{

  public Optional<TimeState> find (
    @NonNull final ApplicationEntityReference<TimeState> identifier
  );

  public default Optional<TimeState> findPrevious (@NonNull final TimeState state) {
    return findPrevious(
      state.getEmittionDate(), 
      ApplicationEntityReference.of(state.getSensor())
    );
  }

  public default Optional<TimeState> findPrevious (
    @NonNull final ZonedDateTime date, 
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    final List<TimeState> result = findPrevious(date, sensor, 1);
    
    return result.size() > 0 ? Optional.ofNullable(result.get(0)) 
                             : Optional.empty();
  }

  public default List<TimeState> findPrevious (
    @NonNull final TimeState state, 
    final int count
  ) {
    return findPrevious(
      state.getEmittionDate(),
      ApplicationEntityReference.of(state.getSensor()),
      count
    );
  }

  public List<TimeState> findPrevious (
    @NonNull final ZonedDateTime date, 
    @NonNull final ApplicationEntityReference<Sensor> sensor, 
    final int count
  );

  public default Optional<TimeState> findNext (
    @NonNull final TimeState state
  ) {
    return findNext(
      state.getEmittionDate(), 
      ApplicationEntityReference.of(state.getSensor())
    );
  }

  public default Optional<TimeState> findNext (
    @NonNull final ZonedDateTime date, 
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    final List<TimeState> results = findNext(date, sensor, 1);
    
    return results.size() > 0 ? Optional.of(results.get(0))
                              : Optional.empty();
  }

  public default List<TimeState> findNext (
    @NonNull final TimeState state, 
    final int count
  ) {
    return findNext(
      state.getEmittionDate(),
      ApplicationEntityReference.of(state.getSensor()),
      count
    );
  }

  public List<TimeState> findNext (
    @NonNull final ZonedDateTime date, 
    @NonNull final ApplicationEntityReference<Sensor> sensor, 
    final int count
  );

  public default List<TimeState> find (
    @NonNull final ApplicationEntityReference<Sensor> sensor, 
    final int count
  ) {
    return find(sensor, 0, count);
  }
  
  public List<TimeState> find (
    @NonNull final ApplicationEntityReference<Sensor> sensor, 
    final int offset, 
    final int count
  );

  public List<TimeState> findAll (
    @NonNull final ApplicationEntityReference<Sensor> sensor
  );
}
