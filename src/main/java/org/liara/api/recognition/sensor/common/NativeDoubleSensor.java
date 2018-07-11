package org.liara.api.recognition.sensor.common;

import org.liara.api.data.entity.state.DoubleState;
import org.liara.api.recognition.sensor.EmitStateOfType;

@EmitStateOfType(DoubleState.class)
public interface NativeDoubleSensor
       extends NativeSensor
{ }
