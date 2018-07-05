package org.liara.api.recognition.sensor.common.virtual.mouvement.onevsall;

import java.util.List;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

public interface OneVsAllToUpDownMotionSensorData
{
  public List<BooleanState> fetchAll (@NonNull final Sensor sensor);

  public BooleanState fetchCorrelated (
    @NonNull final State state,
    @NonNull final Sensor sensor
  );

  public State fetch (@NonNull final Long identifier);
}
