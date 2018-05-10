package org.liara.api.recognition.sensor;

import org.liara.api.event.NodeWasCreatedEvent;
import org.liara.api.event.NodeWillBeCreatedEvent;
import org.liara.api.event.SensorWasCreatedEvent;
import org.liara.api.event.SensorWillBeCreatedEvent;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeCreatedEvent;
import org.liara.api.event.StateWillBeMutatedEvent;
import org.springframework.lang.NonNull;

public interface VirtualSensorHandler
{
  /**
   * A method called when the given virtual sensor is created into the application database.
   * 
   * @param runner The handler related runner.
   */
  public void initialize (@NonNull final VirtualSensorRunner runner);
  
  /**
   * A method called before a shutdown of this sensor.
   */
  public void pause ();
  
  public void sensorWillBeCreated (@NonNull final SensorWillBeCreatedEvent event);
  
  public void sensorWasCreated (@NonNull final SensorWasCreatedEvent event);
  
  public void nodeWillBeCreated (@NonNull final NodeWillBeCreatedEvent event);
  
  public void nodeWasCreated (@NonNull final NodeWasCreatedEvent event);
  
  public void stateWillBeCreated (@NonNull final StateWillBeCreatedEvent event);
  
  public void stateWasCreated (@NonNull final StateWasCreatedEvent event);
  
  public void stateWillBeMutated (@NonNull final StateWillBeMutatedEvent event);
  
  public void stateWasMutated (@NonNull final StateWasMutatedEvent event);
  
  /**
   * A method called when the given virtual sensor is restarted.
   * 
   * @param runner The handler related runner.
   */
  public void resume (@NonNull final VirtualSensorRunner runner);
  
  /**
   * A method called when the given virtual sensor is deleted from the application database (soft delete).
   */
  public void stop ();
}
