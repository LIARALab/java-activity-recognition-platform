package org.liara.api.data.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TimeSeries<TimeState extends State>
{
  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final Class<TimeState> _stateType;
  
  @Autowired
  public TimeSeries (
    @NonNull final Class<TimeState> stateType,
    @NonNull final EntityManager entityManager
  ) { 
    _entityManager = entityManager;
    _stateType = stateType;
  }
  
  public Optional<TimeState> find (@NonNull final Long identifier) {
    return Optional.ofNullable(_entityManager.find(_stateType, identifier));
  }
  
  public Optional<TimeState> findPrevious (
    @NonNull final TimeState state
  ) {
    return findPrevious(state.getEmittionDate(), state.getSensor());
  }
  
  public Optional<TimeState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final Sensor sensor
  ) {
    return findPrevious(date, sensor);
  }
  
  public Optional<TimeState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final Long sensor
  ) {
    final List<TimeState> result = findPrevious(date, sensor, 1);
    
    if (result.size() > 0) {
      return Optional.of(result.get(0));
    } else {
      return Optional.empty();
    }
  }
  
  public List<TimeState> findPrevious (
    @NonNull final TimeState state, 
    final int count
  ) {
    return findPrevious(
      state.getEmittionDate(), 
      state.getSensor(), 
      count
    );
  }
  
  public List<TimeState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final Sensor sensor,
    final int count
  ) {
    return findPrevious(date, sensor.getIdentifier(), count);
  }
  
  public List<TimeState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final Long sensor,
    final int count
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._emittionDate < :date ",
        "   AND state._sensor._identifier = :sensor",
        " ORDER BY state._emittionDate DESC"
      ), _stateType
    ).setParameter("date", date)
     .setParameter("sensor", sensor)
     .setMaxResults(count)
     .getResultList();
  }
  

  public Optional<TimeState> findNext (
    @NonNull final TimeState state
  ) {
    return findNext(state.getEmittionDate(), state.getSensorIdentifier());
  }
  
  public Optional<TimeState> findNext (
    @NonNull final ZonedDateTime date,
    @NonNull final Sensor sensor
  ) {
    return findNext(date, sensor.getIdentifier());
  }
  
  public Optional<TimeState> findNext (
    @NonNull final ZonedDateTime date,
    @NonNull final Long sensor
  ) {
    final List<TimeState> result = findNext(date, sensor, 1);
    
    if (result.size() > 0) {
      return Optional.of(result.get(0));
    } else {
      return Optional.empty();
    }
  }
  
  public List<TimeState> findNext (
    @NonNull final TimeState state, 
    final int count
  ) {
    return findNext(
      state.getEmittionDate(), 
      state.getSensorIdentifier(), 
      count
    );
  }
  
  public List<TimeState> findNext (
    @NonNull final ZonedDateTime date,
    @NonNull final Sensor sensor,
    final int count
  ) {
    return findNext(date, sensor.getIdentifier(), count);
  }
  
  public List<TimeState> findNext (
    @NonNull final ZonedDateTime date,
    @NonNull final Long sensor,
    final int count
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._emittionDate < :date ",
        "   AND state._sensor._identifier = :sensor ",
        " ORDER BY state._emittionDate ASC"
      ), _stateType
    ).setParameter("date", date)
     .setParameter("sensor", sensor)
     .setMaxResults(count)
     .getResultList();
  }
  
  public List<TimeState> find (
    @NonNull final Sensor sensor,
    final int count
  ) {
    return find(sensor.getIdentifier(), count);
  }
  
  public List<TimeState> find (
    @NonNull final Long sensor,
    final int count
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._sensor._identifier = :sensor ",
        " ORDER BY state._emittionDate ASC"
      ), _stateType
    ).setParameter("sensor", sensor)
     .setMaxResults(count)
     .getResultList();
  }
  
  public List<TimeState> findAll (
    @NonNull final Sensor sensor
  ) {
    return findAll(sensor.getIdentifier());
  }
  
  public List<TimeState> findAll (
    @NonNull final Long sensor
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._sensor._identifier = :sensor ",
        " ORDER BY state._emittionDate ASC"
      ), _stateType
    ).setParameter("sensor", sensor)
     .getResultList();
  }
}
