package org.liara.api.recognition.sensor.common;

import org.liara.api.data.entity.state.ValueState;
import org.liara.api.recognition.sensor.EmitStateOfType;

@EmitStateOfType(ValueState.Double.class)
public interface NativeDoubleSensor
       extends NativeSensor
{ }
