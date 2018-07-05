package org.liara.api.recognition.sensor.common.virtual.updown.ceil;

import java.util.List;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.NumericState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.TimeSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DatabaseCeilToUpDownConvertionSensorData implements CeilToUpDownConvertionSensorData
{
  @NonNull
  private final EntityManager _manager;
  
  @NonNull
  private final TimeSeries<NumericState> _timeSeries;
  
  @Autowired
  public DatabaseCeilToUpDownConvertionSensorData (
    @NonNull final EntityManager manager
  ) {
    _manager = manager;
    _timeSeries = new TimeSeries<>(NumericState.class, _manager);
  }
  
  @Override
  public List<NumericState> fetchPrevious (
    @NonNull final NumericState state, 
    final int count
  ) {
    return _timeSeries.findPrevious(state, count);
  }

  @Override
  public List<NumericState> fetchNext (
    @NonNull final NumericState state, 
    final int count
  ) {
    return _timeSeries.findNext(state, count);
  }

  @Override
  public List<NumericState> fetchAll (
    @NonNull final Long sensor
  ) {
    return _timeSeries.findAll(sensor);
  }

  @Override
  public BooleanState fetchCurrentCorrelation (
    @NonNull final NumericState correlated
  ) {
    final List<BooleanState> results = _manager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", BooleanState.class.getName(), " state ",
        " WHERE state._correlations = :correlated ",
        "   AND KEY(state._correlations) = 'current'"
      ), BooleanState.class
    ).setParameter("correlated", correlated)
     .setMaxResults(1)
     .getResultList();
    
    return (results.size() > 0) ? results.get(0) : null;
  }

  @Override
  public State getState (@NonNull final Long identifier) {
    return _manager.find(State.class, identifier);
  }
}
