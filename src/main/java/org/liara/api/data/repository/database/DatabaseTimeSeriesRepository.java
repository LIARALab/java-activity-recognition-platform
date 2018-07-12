package org.liara.api.data.repository.database;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.TimeSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Primary
public class DatabaseTimeSeriesRepository<TimeState extends State> implements TimeSeriesRepository<TimeState>
{
  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final Class<TimeState> _stateType;
  
  @Autowired
  public DatabaseTimeSeriesRepository (
    @NonNull final Class<TimeState> stateType,
    @NonNull final EntityManager entityManager
  ) { 
    _entityManager = entityManager;
    _stateType = stateType;
  }
  
  @Override
  public List<TimeState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final ApplicationEntityReference<Sensor> sensor,
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
     .setParameter("sensor", sensor.getIdentifier())
     .setMaxResults(count)
     .getResultList();
  }
  
  @Override
  public List<TimeState> findNext (
    @NonNull final ZonedDateTime date,
    @NonNull final ApplicationEntityReference<Sensor> sensor,
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
     .setParameter("sensor", sensor.getIdentifier())
     .setMaxResults(count)
     .getResultList();
  }
  
  @Override
  public List<TimeState> find (
    @NonNull final ApplicationEntityReference<Sensor> sensor,
    final int from,
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
    ).setParameter("sensor", sensor.getIdentifier())
     .setMaxResults(count)
     .setFirstResult(from)
     .getResultList();
  }
  
  @Override
  public List<TimeState> findAll (
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._sensor._identifier = :sensor ",
        " ORDER BY state._emittionDate ASC"
      ), _stateType
    ).setParameter("sensor", sensor.getIdentifier())
     .getResultList();
  }

  @Override
  public Optional<TimeState> find (
    @NonNull final ApplicationEntityReference<TimeState> reference
  ) {
    return Optional.ofNullable(
      _entityManager.find(reference.getType(), reference.getIdentifier())
    );
  }
}
