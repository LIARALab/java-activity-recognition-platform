package org.liara.api.data.repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

import groovy.lang.Range;

public interface TimeSeriesRepository<TimeState extends State>
       extends ApplicationEntityRepository<TimeState>
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
  
  public default List<TimeState> getAt (
    @NonNull final ApplicationEntityReference<Sensor> sensor, 
    @NonNull final Range<Integer> range
  ) {
    return find(sensor, range.getFrom(), range.getTo() - range.getFrom());
  }

  public List<TimeState> findAll (
    @NonNull final ApplicationEntityReference<Sensor> sensor
  );
  
  public List<TimeState> findWithCorrelation (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  );
  
  public List<TimeState> findWithCorrelations (
    @NonNull final Map<String, ApplicationEntityReference<? extends State>> correlations,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  );
  
  public default Optional<TimeState> findFirstWithCorrelation (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    final List<TimeState> results = findWithCorrelation(name, correlated, sensor);
    
    return (results.size() > 0) ? Optional.ofNullable(results.get(0)) 
                                : Optional.empty(); 
  }
  
  public default Optional<TimeState> findFirstWithCorrelations (
    @NonNull final Map<String, ApplicationEntityReference<? extends State>> correlations,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    final List<TimeState> results = findWithCorrelations(correlations, sensor);
    
    return (results.size() > 0) ? Optional.ofNullable(results.get(0)) 
                                : Optional.empty();
  }
  
  public List<TimeState> findWithAnyCorrelation (
    @NonNull final Collection<String> keys,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  );
  
  public default Optional<TimeState> findFirstWithAnyCorrelation (
    @NonNull final Collection<String> keys,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    final List<TimeState> results = findWithAnyCorrelation(keys, correlated, sensor);
    
    return (results.size() > 0) ? Optional.ofNullable(results.get(0)) 
                                : Optional.empty();
  }
 
  public default Optional<TimeState> findAt (
    @NonNull final ApplicationEntityReference<Sensor> sensor, 
    final int index
  ) {
    final List<TimeState> result = findAll(sensor);
    
    if (index < result.size()) {
      return Optional.ofNullable(result.get(index));
    }
    
    return Optional.empty();
  }
  
  public default TimeState getAt (
    @NonNull final ApplicationEntityReference<Sensor> sensor, 
    final int index
  ) {
    return findAt(sensor, index).get();
  }
  
  public default Optional<TimeState> findFirst (
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    return findAt(sensor, 0);
  }
  
  public default Optional<TimeState> findLast (
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    final List<TimeState> result = findAll(sensor);
    
    if (result.size() > 0) {
      return Optional.ofNullable(result.get(result.size() - 1));
    }
    
    return Optional.empty();
  }
}
