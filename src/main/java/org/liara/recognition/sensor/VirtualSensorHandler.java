package org.liara.recognition.sensor;

import org.springframework.lang.NonNull;

public interface VirtualSensorHandler
{
  /**
   * A method called when the given virtual sensor is created into the application database.
   * 
   * @param runner The handler related runner.
   */
  public void afterCreation (@NonNull final VirtualSensorRunner runner);
  
  /**
   * A method called when the given virtual sensor is restarted.
   * 
   * @param runner The handler related runner.
   */
  public void afterRestart (@NonNull final VirtualSensorRunner runner);
  
  /**
   * A method called before a shutdown of this sensor.
   */
  public void beforeStop ();
  
  /**
   * A method called when the given virtual sensor is deleted from the application database (soft delete).
   */
  public void beforeDeletion ();
}
