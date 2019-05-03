package org.liara.api.recognition.sensor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.node.DidCreateNodeEvent;
import org.liara.api.event.node.WillCreateNodeEvent;
import org.liara.api.event.sensor.SensorWasCreatedEvent;
import org.liara.api.event.sensor.SensorWillBeCreatedEvent;
import org.liara.api.event.state.DidCreateStateEvent;
import org.liara.api.event.state.DidUpdateStateEvent;
import org.liara.api.event.state.WillCreateStateEvent;
import org.liara.api.event.state.WillUpdateStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class VirtualSensorManager
{
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
      throw new Error(
        "Unnable to register a runner created for another virtual sensor manager instance.");
    }

    if (!_runners.containsValue(runner)) {
      getLogger().info(
        "Registering new virtual sensor runner : " + runner.getHandler().getClass().getName() +
        "#" + runner.getSensor().getIdentifier());
      _runners.put(runner.getSensor().getIdentifier(), runner);
    }
  }

  public void unregisterRunner (@NonNull final VirtualSensorRunner runner) {
    if (runner.getManager() != this) {
      throw new Error(
        "Unnable to unregister a runner created for another virtual sensor manager instance.");
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
    getLogger().info("Virtual sensor manager initialization...");
    getLogger().info("Finding virtual sensors in application database...");

    @NonNull final List<@NonNull Sensor> sensors = _entityManager.createQuery(
      "SELECT sensor FROM " + Sensor.class.getName() + " sensor",
      Sensor.class
    ).getResultList();

    for (@NonNull final Sensor sensor : sensors) {
      if (!sensor.getTypeInstance().isNative()) {
        VirtualSensorRunner.restart(this, sensor);
      }
    }
  }

  @PreDestroy
  public void beforeApplicationShutdown () {
    getLogger().info("Virtual sensor manager destruction...");
    getLogger().info("Stopping all running virtual sensors...");

    for (@NonNull final VirtualSensorRunner runner : _runners.values()) {
      runner.pause();
    }

    _runners.clear();
  }

  @EventListener
  public void sensorWillBeCreated (final SensorWillBeCreatedEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().sensorWillBeCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "sensorWillBeCreated", error);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) error.printStackTrace();
      throw error;
    }
  }

  @EventListener
  public void sensorWasCreated (final SensorWasCreatedEvent event) {
    final Sensor sensor = event.getSensor();

    if (VirtualSensorHandler.isVirtual(sensor)) {
      Objects.requireNonNull(event.getSensor().getIdentifier());
      VirtualSensorRunner.create(this, sensor);
    }

    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().sensorWasCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "sensorWasCreated", error);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) error.printStackTrace();
      throw error;
    }
  }

  @EventListener
  public void nodeWillBeCreated (final WillCreateNodeEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().nodeWillBeCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "nodeWillBeCreated", error);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) error.printStackTrace();
      throw error;
    }
  }

  @EventListener
  public void nodeWasCreated (final DidCreateNodeEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().nodeWasCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "nodeWasCreated", error);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) error.printStackTrace();
      throw error;
    }
  }

  @EventListener
  public void stateWillBeCreated (final WillCreateStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWillBeCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWillBeCreated", error);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) error.printStackTrace();
      throw error;
    }
  }

  @EventListener
  public void stateWasCreated (final DidCreateStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWasCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWasCreated", error);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) error.printStackTrace();
      throw error;
    }
  }

  @EventListener
  public void stateWillBeMutated (final WillUpdateStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWillBeMutated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWillBeMutated", error);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) error.printStackTrace();
      throw error;
    }
  }

  @EventListener
  public void stateWasMutated (final DidUpdateStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWasMutated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWasMutated", error);
      if (getLogger().getLevel().intValue() <= Level.FINE.intValue()) error.printStackTrace();
      throw error;
    }
  }

  private @NonNull Logger getLogger () {
    return Logger.getLogger(getClass().getName());
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
