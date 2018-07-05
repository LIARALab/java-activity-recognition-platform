package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import java.util.List;

import javax.persistence.EntityManager;

import org.liara.api.collection.Operators;
import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.data.collection.BooleanStateCollection;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.sensor.Sensor_;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.BooleanState_;
import org.liara.api.data.entity.state.State;
import org.liara.api.recognition.sensor.common.NativeMotionSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DatabaseOneVsAllToUpDownMotionSensor 
       implements OneVsAllToUpDownMotionSensorData
{
  @NonNull
  private final EntityManager _entityManager;
  
  @Autowired
  public DatabaseOneVsAllToUpDownMotionSensor (
    @NonNull final EntityManager manager
  ) { _entityManager = manager; }
  
  
  public BooleanStateCollection getWatchedMotionTicksCollection (
    @NonNull final Sensor sensor
  ) {
    return new BooleanStateCollection(_entityManager).of(
      new SensorCollection(_entityManager).deepIn(sensor.getNode())
                                          .ofType(NativeMotionSensor.class)
    ).apply(Operators.equal("_value", true));
  }
  
  @Override
  public List<BooleanState> fetchAll (@NonNull final Sensor sensor) {
    final BooleanStateCollection watched = getWatchedMotionTicksCollection(sensor);
    
    final EntityCollectionMainQuery<BooleanState, BooleanState> query = watched.apply(
      Operators.orderAscendingBy(x -> x.get(BooleanState_._emittionDate))
    ).createCollectionQuery();
    
    query.join(x -> x.join(BooleanState_._sensor))
         .join(x -> x.join(Sensor_._node));
    
    return query.fetchAll();
  }

  @Override
  public BooleanState fetchCorrelated (
    @NonNull final State state,
    @NonNull final Sensor sensor
  ) {
    final List<BooleanState> results = _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", BooleanState.class.getName(), " state ",
        " WHERE state._correlations = :correlated ",
        "   AND KEY(state._correlations) = 'base' ",
        "   AND state._sensor = :sensor"
      ), BooleanState.class
    ).setParameter("correlated", state)
     .setParameter("sensor", sensor)
     .setMaxResults(1)
     .getResultList();
    
    return (results.size() > 0) ? results.get(0) : null;
  }

  @Override
  public State fetch (@NonNull final Long identifier) {
    return _entityManager.find(State.class, identifier);
  }

}
