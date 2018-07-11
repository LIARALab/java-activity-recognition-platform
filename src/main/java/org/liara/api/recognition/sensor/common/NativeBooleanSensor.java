package org.liara.api.recognition.sensor.common;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.recognition.sensor.EmitStateOfType;

@EmitStateOfType(BooleanState.class)
public interface NativeBooleanSensor 
       extends NativeSensor
{ }
