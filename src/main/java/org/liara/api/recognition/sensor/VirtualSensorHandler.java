package org.liara.api.recognition.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.NodeEvent;
import org.liara.api.event.SensorEvent;
import org.liara.api.event.StateEvent;

public interface VirtualSensorHandler
{
  static boolean isVirtual (@NonNull final Sensor sensor) {
    return !sensor.getTypeInstance().isNative();
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

  default void sensorWillBeCreated (final SensorEvent.@NonNull WillBeCreated event) {}

  default void sensorWasCreated (final SensorEvent.@NonNull WasCreated event) {}

  default void nodeWillBeCreated (final NodeEvent.@NonNull WillBeCreated event) {}

  default void nodeWasCreated (final NodeEvent.@NonNull WasCreated event) {}

  default void stateWillBeCreated (final StateEvent.@NonNull WillBeCreated event) {}

  default void stateWasCreated (final StateEvent.@NonNull WasCreated event) {}

  default void stateWillBeMutated (final StateEvent.@NonNull WillBeMutated event) {}

  default void stateWasMutated (final StateEvent.@NonNull WasMutated event) {}

  default void stateWillBeDeleted (final StateEvent.@NonNull WillBeDeleted event) {}

  default void stateWasDeleted (final StateEvent.@NonNull WasDeleted event) {}

  /**
   * A method called when the given virtual sensor is restarted.
   * 
   * @param runner The handler related runner.
   */
  void resume (@NonNull final VirtualSensorRunner runner);
  
  /**
   * A method called when the given virtual sensor is deleted from the application database (soft doDelete).
   */
  void stop ();
}
