package org.liara.api.recognition.sensor;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.liara.api.data.entity.state.State;

@Retention(RUNTIME)
@Target(TYPE)
public @interface EmitStateOfType
{
  public Class<? extends State> value ();
}
