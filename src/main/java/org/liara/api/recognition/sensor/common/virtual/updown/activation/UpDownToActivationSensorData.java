package org.liara.api.recognition.sensor.common.virtual.updown.activation;

import java.time.ZonedDateTime;
import java.util.List;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

public interface UpDownToActivationSensorData
{
  public default List<BooleanState> fetchStates (
    @NonNull final Sensor sensor
  ) {
    return fetchStates(sensor.getIdentifier());
  }
  
  public List<BooleanState> fetchStates (
    @NonNull final Long emitter
  );

  public default BooleanState previous (
    @NonNull final BooleanState current
  ) {
    return previous(current.getEmittionDate(), current.getSensor());
  }
  
  public default BooleanState next (
    @NonNull final BooleanState current
  ) {
    return next(current.getEmittionDate(), current.getSensor());
  }

  public BooleanState next (
    @NonNull final ZonedDateTime time, 
    @NonNull final Sensor sensor
  );

  public BooleanState previous (
    @NonNull final ZonedDateTime time, 
    @NonNull final Sensor sensor
  );
  
  public ActivationState activationOf (
    @NonNull final BooleanState target,
    @NonNull final Sensor emitter
  );

  public ActivationState previousActivation (
    @NonNull final BooleanState current, 
    @NonNull final Sensor sensor
  );

  public ActivationState nextActivation (
    @NonNull final BooleanState current, 
    @NonNull final Sensor sensor
  );

  public ActivationState correlatedState (
    @NonNull final BooleanState next, 
    @NonNull final Sensor sensor
  );

  public State getState (
    @NonNull final Long identifier
  );
}
