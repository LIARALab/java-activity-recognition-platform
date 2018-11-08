package org.liara.api.recognition.sensor.common;

import org.liara.api.data.entity.state.ValueState;
import org.liara.api.recognition.sensor.EmitStateOfType;

@EmitStateOfType(ValueState.Boolean.class)
public interface NativeMotionSensor 
       extends   NativeSensor 
{ }
