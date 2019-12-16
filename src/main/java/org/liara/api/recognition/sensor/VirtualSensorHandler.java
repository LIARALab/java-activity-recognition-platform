package org.liara.api.recognition.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.node.DidCreateNodeEvent;
import org.liara.api.event.node.WillCreateNodeEvent;
import org.liara.api.event.sensor.DidCreateSensorEvent;
import org.liara.api.event.sensor.DidDeleteSensorEvent;
import org.liara.api.event.sensor.WillCreateSensorEvent;
import org.liara.api.event.sensor.WillDeleteSensorEvent;
import org.liara.api.event.state.*;

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

  default void sensorWillBeCreated (final WillCreateSensorEvent event) {}

  default void sensorWasCreated (final DidCreateSensorEvent event) {}

  default void sensorWillBeDeleted (final WillDeleteSensorEvent event) {}

  default void sensorWasDeleted (final DidDeleteSensorEvent event) {}

  default void nodeWillBeCreated (final WillCreateNodeEvent event) {}

  default void nodeWasCreated (final DidCreateNodeEvent event) {}

  default void stateWillBeCreated (final WillCreateStateEvent event) {}

  default void stateWasCreated (final DidCreateStateEvent event) {}

  default void stateWillBeMutated (final WillUpdateStateEvent event) {}

  default void stateWasMutated (final DidUpdateStateEvent event) {}

  default void stateWillBeDeleted (final WillDeleteStateEvent event) {}

  default void stateWasDeleted (final DidDeleteStateEvent event) {}

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
