package org.liara.api.recognition.sensor.common.virtual.presence;

import java.time.ZonedDateTime;
import java.util.List;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

public interface PresenceSensorData
{
  public List<BooleanState> getTrackedMotionActivation (@NonNull final Sensor sensor);

  public boolean isTracked (
    @NonNull final Sensor sensor, 
    @NonNull final State state
  );
  
  public ActivationState getActivationAfter (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  );
  
  public ActivationState getActivationBefore (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  );
  
  public BooleanState getTickAfter (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  );
  
  public BooleanState getTickBefore (
    @NonNull final Sensor sensor, 
    @NonNull final ZonedDateTime date
  );
}
