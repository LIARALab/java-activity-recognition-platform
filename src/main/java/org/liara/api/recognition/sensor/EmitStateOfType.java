package org.liara.api.recognition.sensor;

import org.liara.api.data.entity.state.State;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface EmitStateOfType
{
  Class<? extends State> value ();
}
