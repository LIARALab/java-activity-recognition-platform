package org.liara.api.recognition.sensor;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.event.NodeEvent;
import org.liara.api.event.SensorEvent;
import org.liara.api.event.StateEvent;
import org.springframework.lang.NonNull;

public interface VirtualSensorHandler
{
  static boolean isVirtual (@NonNull final Sensor sensor) {
    return VirtualSensorHandler.class.isAssignableFrom(sensor.getTypeClass());
  }

  static @NonNull
  Class<? extends State> emittedStateOf (@NonNull final Sensor sensor) {
    final Class<?>        typeClass  = sensor.getTypeClass();
    final EmitStateOfType annotation = typeClass.getAnnotation(EmitStateOfType.class);

    if (annotation == null) {
      throw new Error(String.join(
        "",
        "Unnable to retrieve the emitted state type of this sensor because the ",
        "type of this sensor ",
        typeClass.toString(),
        " does not declare any ",
        EmitStateOfType.class.toString(),
        " annotation."
      ));
    } else {
      return annotation.value();
    }
  }

  /**
   * A method called when the given virtual sensor is created into the application database.
   * 
   * @param runner The handler related runner.
   */
  void initialize (@NonNull final VirtualSensorRunner runner);
  
  /**
   * A method called before a shutdown of this sensor.
   */
  void pause ();

  default void sensorWillBeCreated (@NonNull final SensorWillBeCreatedEvent event) {}

  default void sensorWasCreated (@NonNull final SensorEvent event) {}

  default void nodeWillBeCreated (@NonNull final NodeWillBeCreatedEvent event) {}

  default void nodeWasCreated (@NonNull final NodeEvent event) {}

  default void stateWillBeCreated (@NonNull final StateWillBeCreatedEvent event) {}

  default void stateWasCreated (@NonNull final StateEvent event) {}

  default void stateWillBeMutated (@NonNull final StateWillBeMutatedEvent event) {}

  default void stateWasMutated (@NonNull final StateWasMutatedEvent event) {}

  default void stateWillBeDeleted (@NonNull final StateWillBeDeletedEvent event) {}

  default void stateWasDeleted (@NonNull final StateWasDeletedEvent event) {}

  /**
   * A method called when the given virtual sensor is restarted.
   * 
   * @param runner The handler related runner.
   */
  void resume (@NonNull final VirtualSensorRunner runner);
  
  /**
   * A method called when the given virtual sensor is deleted from the application database (soft delete).
   */
  void stop ();
}
