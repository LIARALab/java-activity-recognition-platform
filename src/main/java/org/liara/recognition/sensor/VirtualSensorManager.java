package org.liara.recognition.sensor;

import javax.annotation.PreDestroy;

import org.jboss.logging.Logger;
import org.liara.api.data.collection.SensorCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class VirtualSensorManager
{
  @NonNull
  private final SensorCollection _sensors;
  
  @NonNull
  private final Logger _logger = Logger.getLogger(VirtualSensorManager.class);
  
  @Autowired
  public VirtualSensorManager (
    @NonNull final SensorCollection sensors
  ) { _sensors = sensors; }
  
  @EventListener
  public void afterApplicationInitialization (@NonNull final ContextStartedEvent event) {
    _logger.debug("Virtual sensor manager initialization...");
    _logger.debug("Finding virtual sensors in application database...");
    
    
  }
  
  @PreDestroy
  public void beforeApplicationShutdown () {
    _logger.debug("Virtual sensor manager destruction...");
    _logger.debug("Stopping all running virtual sensors...");
    
  }
}
