package org.liara.api.recognition.sensor;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.jboss.logging.Logger;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.event.SensorWasCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

@Service
public class VirtualSensorManager
{
  @NonNull
  private final ApplicationContext _applicationContext;
  
  @NonNull
  private final SensorCollection _sensors;
  
  @NonNull
  private final Logger _logger = Logger.getLogger(VirtualSensorManager.class);
  
  @NonNull
  private final BiMap<Long, VirtualSensorRunner> _runners = HashBiMap.create();
  
  @Autowired
  public VirtualSensorManager (
    @NonNull final ApplicationContext applicationContext,
    @NonNull final SensorCollection sensors
  ) { 
    _applicationContext = applicationContext;
    _sensors = sensors; 
  }
  
  public void registerRunner (@NonNull final VirtualSensorRunner runner) {
    if (runner.getManager() != this) {
      throw new Error("Unnable to register a runner created for another virtual sensor manager instance.");
    }
    
    if (!_runners.containsValue(runner)) {
      _logger.info("Registering new virtual sensor runner : " + runner.toString());
      _runners.put(runner.getSensor().getIdentifier(), runner);
    }
  }
  
  public void unregisterRunner (@NonNull final VirtualSensorRunner runner) {
    if (runner.getManager() != this) {
      throw new Error("Unnable to unregister a runner created for another virtual sensor manager instance.");
    }
    
    if (_runners.containsValue(runner)) {
      switch (runner.getState()) {
        case STOPPED:
        case PAUSED:
          _runners.remove(runner.getSensor().getIdentifier());
          break;
        default:
          runner.pause();
          break;
      }
    }
  }
  
  public void start () {
    _logger.info("Virtual sensor manager initialization...");
    _logger.info("Finding virtual sensors in application database...");
    
    
  }
  
  @EventListener
  public void onSensorCreation (@NonNull final SensorWasCreatedEvent event) {
    if (event.getSensor().isVirtual()) {
      VirtualSensorRunner.create(this, event.getSensor());
    }
  }
  
  @PreDestroy
  public void beforeApplicationShutdown () {
    _logger.info("Virtual sensor manager destruction...");
    _logger.info("Stopping all running virtual sensors...");
    
  }
  
  public VirtualSensorRunner getRunner (@NonNull final Long identifier) {
    return _runners.get(identifier);
  }
  
  public VirtualSensorRunner getRunner (@NonNull final Sensor sensor) {
    return _runners.get(sensor.getIdentifier());
  }
  
  public Set<VirtualSensorRunner> getRunners () {
    return Collections.unmodifiableSet(_runners.values());
  }
  
  public Iterator<VirtualSensorRunner> runners () {
    return getRunners().iterator();
  }
  
  public ApplicationContext getApplicationContext () {
    return _applicationContext;
  }
}
