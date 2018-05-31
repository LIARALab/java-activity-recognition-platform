package org.liara.api.recognition.sensor.common.virtual.presence;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.liara.api.collection.Operators;
import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.data.collection.ActivationStateCollection;
import org.liara.api.data.collection.BooleanStateCollection;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.liara.api.recognition.sensor.common.NativeMotionSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DatabasePresenceSensorData implements PresenceSensorData
{
  @NonNull
  private final EntityManager _manager;
  
  @Autowired
  public DatabasePresenceSensorData (
    @NonNull final EntityManager manager
  ) {
    _manager = manager;
  }
  
  @Override
  public List<BooleanState> getTrackedMotionActivation (
    @NonNull final Sensor sensor
  ) {
    final BooleanStateCollection watched = getWatchedMotionTicksCollection(sensor);
    
    final EntityCollectionMainQuery<BooleanState, BooleanState> query = watched.apply(
      Operators.orderAscendingBy(x -> x.get("_emittionDate"))
    ).createCollectionQuery();
    
    query.join(x -> x.join("_sensor")).join(x -> x.join("_node"));
    
    final List<BooleanState> ticks = _manager.createQuery(query.getCriteriaQuery())
                                             .getResultList();
    
    _manager.clear();
    
    return ticks;
  }

  public BooleanStateCollection getWatchedMotionTicksCollection (
    @NonNull final Sensor sensor
  ) {
    return new BooleanStateCollection(_manager).of(
      new SensorCollection(_manager).deepIn(sensor.getNode())
                                    .ofType(NativeMotionSensor.class)
    ).apply(Operators.equal("_value", true));
  }

  @Override
  public boolean isTracked (@NonNull final Sensor sensor, @NonNull final State state) {
    return new SensorCollection(_manager).deepIn(
      sensor.getNode()
    ).containsEntityWithIdentifier(
      state.getSensorIdentifier()
    );
  }

  @Override
  public ActivationState getActivationAfter (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  ) {
    return new ActivationStateCollection(
      _manager
    ).of(sensor)
     .afterOrAt(date)
     .apply(Operators.orderAscendingBy("_emittionDate"))
     .first()
     .orElse(null);
  }

  @Override
  public ActivationState getActivationBefore (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  ) {
    return new ActivationStateCollection(
      _manager
    ).of(sensor)
     .beforeOrAt(date)
     .apply(Operators.orderDescendingBy("_emittionDate"))
     .first()
     .orElse(null);
  }

  @Override
  public BooleanState getTickAfter (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  ) {
    return getWatchedMotionTicksCollection(
      sensor
    ).after(date)
     .apply(Operators.orderAscendingBy("_emittionDate"))
     .first()
     .orElse(null);
  }

  @Override
  public BooleanState getTickBefore (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  ) {
    return getWatchedMotionTicksCollection(
      sensor
    ).before(date)
     .apply(Operators.orderAscendingBy("_emittionDate"))
     .first()
     .orElse(null);
  }
}
