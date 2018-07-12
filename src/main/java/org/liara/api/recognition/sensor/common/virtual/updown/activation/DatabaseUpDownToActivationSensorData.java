package org.liara.api.recognition.sensor.common.virtual.updown.activation;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.TimeSeriesRepository;
import org.liara.api.data.repository.database.DatabaseTimeSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DatabaseUpDownToActivationSensorData implements UpDownToActivationSensorData
{
  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final TimeSeriesRepository<BooleanState> _flagTimeSeries;
  
  @NonNull
  private final TimeSeriesRepository<ActivationState> _activationTimeSeries;
  
  @Autowired
  public DatabaseUpDownToActivationSensorData (
    @NonNull final EntityManager entityManager
  ) {
    _entityManager = entityManager;
    _flagTimeSeries = new DatabaseTimeSeriesRepository<>(BooleanState.class, _entityManager);
    _activationTimeSeries = new DatabaseTimeSeriesRepository<>(ActivationState.class, _entityManager);
  }

  @Override
  public List<BooleanState> fetchStates (@NonNull final Long emitter) {
    return _flagTimeSeries.findAll(emitter);
  }

  @Override
  public BooleanState next (
    @NonNull final ZonedDateTime time, 
    @NonNull final Sensor sensor
  ) {
    return _flagTimeSeries.findNext(time, sensor).orElse(null);
  }

  @Override
  public BooleanState previous (
    @NonNull final ZonedDateTime time, 
    @NonNull final Sensor sensor
  ) {
    return _flagTimeSeries.findPrevious(time, sensor).orElse(null);
  }

  @Override
  public ActivationState activationOf (
    @NonNull final BooleanState target, 
    @NonNull final Sensor emitter
  ) {
    final List<ActivationState> result = _entityManager.createQuery(
      String.join(
        "",
        "SELECT activation ",
        "  FROM ", ActivationState.class.getName(), " activation ",
        " WHERE activation._start <= :date ",
        "   AND activation._end >= :date ",
        "   AND activation._sensor = :sensor"
      ), ActivationState.class
    ).setParameter("date", target.getEmittionDate())
     .setParameter("sensor", emitter)
     .setMaxResults(1)
     .getResultList();
    
    return (result.size() > 0) ? result.get(0) : null;
  }

  @Override
  public ActivationState previousActivation (
    @NonNull final BooleanState current, 
    @NonNull final Sensor sensor
  ) {
    return _activationTimeSeries.findPrevious(
      current.getEmittionDate(), sensor
    ).orElse(null);
  }

  @Override
  public ActivationState nextActivation (
    @NonNull final BooleanState current, 
    @NonNull final Sensor sensor
  ) {
    return _activationTimeSeries.findNext(
      current.getEmittionDate(), sensor
    ).orElse(null);
  }

  @Override
  public ActivationState correlatedState (
    @NonNull final BooleanState next, 
    @NonNull final Sensor sensor
  ) {
    final List<ActivationState> results = _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", ActivationState.class.getName(), " state ",
        " WHERE state._correlations = :correlated ",
        "   AND KEY(state._correlations) IN ('start', 'end') ",
        "   AND state._sensor = :sensor"
      ), ActivationState.class
    ).setParameter("correlated", next)
     .setParameter("sensor", sensor)
     .setMaxResults(1)
     .getResultList();
    
    return (results.size() > 0) ? results.get(0) : null;
  }

  @Override
  public State getState (
    @NonNull final Long identifier
  ) {
    return _entityManager.find(State.class, identifier);
  }
}
