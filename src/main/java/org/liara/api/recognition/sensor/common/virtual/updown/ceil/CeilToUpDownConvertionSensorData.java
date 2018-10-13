package org.liara.api.recognition.sensor.common.virtual.updown.ceil;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.NumericState;
import org.liara.api.data.entity.state.State;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CeilToUpDownConvertionSensorData
{
  public default NumericState fetchPrevious (@NonNull final NumericState state) {
    final List<NumericState> result = fetchPrevious(state, 1);
    return (result.size() > 0) ? result.get(0) : null;
  }
  
  public default NumericState fetchNext (@NonNull final NumericState state) {
    final List<NumericState> result = fetchNext(state, 1);
    return (result.size() > 0) ? result.get(0) : null;
  }
  
  public List<NumericState> fetchPrevious (@NonNull final NumericState state, final int count);
  public List<NumericState> fetchNext (@NonNull final NumericState state, final int count);
  
  public default List<NumericState> fetchAll (@NonNull final Sensor sensor) {
    return fetchAll(sensor.getIdentifier());
  }
  
  public List<NumericState> fetchAll (@NonNull final Long sensor);

  public BooleanState fetchCurrentCorrelation (@NonNull final NumericState next);

  public State getState (@NonNull final Long identifier);
}
