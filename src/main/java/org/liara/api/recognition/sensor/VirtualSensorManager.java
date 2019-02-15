package org.liara.api.recognition.sensor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jboss.logging.Logger;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.NodeEvent;
import org.liara.api.event.SensorEvent;
import org.liara.api.event.StateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class VirtualSensorManager
{
  @NonNull
  private final Logger _logger = Logger.getLogger(VirtualSensorManager.class);

  @NonNull
  private final ApplicationContext _applicationContext;

  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final BiMap<@NonNull Long, @NonNull VirtualSensorRunner> _runners = HashBiMap.create();
  
  @Autowired
  public VirtualSensorManager (
    @NonNull final ApplicationContext applicationContext, @NonNull final EntityManager entityManager
  ) { 
    _applicationContext = applicationContext;
    _entityManager = entityManager;
  }
  
  public void registerRunner (@NonNull final VirtualSensorRunner runner) {
    if (runner.getManager() != this) {
      throw new Error("Unnable to register a runner created for another virtual sensor manager instance.");
    }
    
    if (!_runners.containsValue(runner)) {
      _logger.info("Registering new virtual sensor runner : " + runner.getHandler().getClass().getName() + "#" + runner.getSensor().getIdentifier());
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

    @NonNull final List<@NonNull Sensor> sensors = _entityManager.createQuery(String.join(
      "", "SELECT sensor FROM ",
      Sensor.class.getName(), " sensor"
    ), Sensor.class).getResultList();

    for (@NonNull final Sensor sensor : sensors) {
      if (!sensor.getTypeInstance().isNative()) {
        VirtualSensorRunner.restart(this, sensor);
      }
    }
  }
  
  @EventListener
  public void onSensorCreation (@NonNull final SensorEvent event) {
    final Sensor sensor = event.getSensor();
    if (VirtualSensorHandler.isVirtual(sensor)) {
      VirtualSensorRunner.create(this, sensor);
    }
  }
  
  @PreDestroy
  public void beforeApplicationShutdown () {
    _logger.info("Virtual sensor manager destruction...");
    _logger.info("Stopping all running virtual sensors...");

    for (@NonNull final VirtualSensorRunner runner : _runners.values()) {
      runner.pause();
    }
    
    _runners.clear();
  }
  
  @EventListener
  public void sensorWillBeCreated (final SensorEvent.@NonNull WillBeCreated event) {
    for (final VirtualSensorRunner runner : _runners.values()) {
      runner.getHandler().sensorWillBeCreated(event);
    }
  }

  @EventListener
  public void sensorWasCreated (final SensorEvent.@NonNull WasCreated event) {
    for (final VirtualSensorRunner runner : _runners.values()) {
      runner.getHandler().sensorWasCreated(event);
    }
  }

  @EventListener
  public void nodeWillBeCreated (final NodeEvent.@NonNull WillBeCreated event) {
    for (final VirtualSensorRunner runner : _runners.values()) {
      runner.getHandler().nodeWillBeCreated(event);
    }
  }

  @EventListener
  public void nodeWasCreated (final NodeEvent.@NonNull WasCreated event) {
    for (final VirtualSensorRunner runner : _runners.values()) {
      runner.getHandler().nodeWasCreated(event);
    }
  }

  @EventListener
  public void stateWillBeCreated (final StateEvent.@NonNull WillBeCreated event) {
    for (final VirtualSensorRunner runner : _runners.values()) {
      runner.getHandler().stateWillBeCreated(event);
    }
  }

  @EventListener
  public void stateWasCreated (final StateEvent.@NonNull WasCreated event) {
    for (final VirtualSensorRunner runner : _runners.values()) {
      runner.getHandler().stateWasCreated(event);
    }
  }

  @EventListener
  public void stateWillBeMutated (final StateEvent.@NonNull WillBeMutated event) {
    for (final VirtualSensorRunner runner : _runners.values()) {
      runner.getHandler().stateWillBeMutated(event);
    }
  }

  @EventListener
  public void stateWasMutated (final StateEvent.@NonNull WasMutated event) {
    for (final VirtualSensorRunner runner : _runners.values()) {
      runner.getHandler().stateWasMutated(event);
    }
  }

  public @NonNull VirtualSensorRunner getRunner (@NonNull final Long identifier) {
    return _runners.get(identifier);
  }

  public @NonNull VirtualSensorRunner getRunner (@NonNull final Sensor sensor) {
    return _runners.get(sensor.getIdentifier());
  }

  public @NonNull Set<@NonNull VirtualSensorRunner> getRunners () {
    return Collections.unmodifiableSet(_runners.values());
  }

  public @NonNull Iterator<@NonNull VirtualSensorRunner> runners () {
    return getRunners().iterator();
  }

  public @NonNull ApplicationContext getApplicationContext () {
    return _applicationContext;
  }
}
