package org.liara.api.recognition.sensor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.event.node.DidCreateNodeEvent;
import org.liara.api.event.node.WillCreateNodeEvent;
import org.liara.api.event.sensor.DidCreateSensorEvent;
import org.liara.api.event.sensor.DidDeleteSensorEvent;
import org.liara.api.event.sensor.WillCreateSensorEvent;
import org.liara.api.event.sensor.WillDeleteSensorEvent;
import org.liara.api.event.state.*;
import org.liara.api.event.system.ApplicationResetEvent;
import org.liara.api.logging.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import java.util.*;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class VirtualSensorManager
  implements Loggable
{
  @NonNull
  private final ApplicationContext _applicationContext;

  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final BiMap<@NonNull Long, @NonNull VirtualSensorRunner> _runners = HashBiMap.create();

  @Autowired
  public VirtualSensorManager (
    @NonNull final ApplicationContext applicationContext,
    @NonNull final EntityManager entityManager
  ) {
    _applicationContext = applicationContext;
    _entityManager = entityManager;
  }

  public void registerRunner (@NonNull final VirtualSensorRunner runner) {
    if (runner.getManager() != this) {
      throw new Error(
        "Unable to register a runner created for another virtual sensor manager instance.");
    }

    if (!_runners.containsValue(runner)) {
      info(
        "Registering new virtual sensor runner : " + runner.getHandler().getClass().getName() +
        "#" + runner.getSensor().getIdentifier()
      );
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
    info("Virtual sensor manager initialization...");
    info("Finding virtual sensors in application database...");

    @NonNull final List<@NonNull Sensor> sensors = _entityManager.createQuery(
      "SELECT sensor FROM " + Sensor.class.getName() + " sensor",
      Sensor.class
    ).getResultList();

    for (@NonNull final Sensor sensor : sensors) {
      Objects.requireNonNull(sensor.getTypeInstance());

      if (!sensor.getTypeInstance().isNative()) {
        VirtualSensorRunner.restart(this, sensor);
      }
    }
  }

  @PreDestroy
  public void beforeApplicationShutdown () {
    info("Virtual sensor manager destruction...");
    info("Stopping all running virtual sensors...");

    for (@NonNull final VirtualSensorRunner runner : _runners.values()) {
      runner.pause();
    }

    _runners.clear();
  }

  @EventListener
  public void reset (@NonNull final ApplicationResetEvent event) {
    info("Application reset...");
    info("Destroying all running virtual sensors...");

    for (@NonNull final VirtualSensorRunner runner : _runners.values()) {
      runner.stop();
    }

    _runners.clear();
  }

  @EventListener
  public void sensorWillBeCreated (@NonNull final WillCreateSensorEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().sensorWillBeCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "sensorWillBeCreated", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void sensorWasCreated (@NonNull final DidCreateSensorEvent event) {
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
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void sensorWillBeDeleted (@NonNull final WillDeleteSensorEvent event) {
    @NonNull final Sensor sensor = event.getSensor();

    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().sensorWillBeDeleted(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "sensorWillBeDeleted", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void sensorWasDeleted (@NonNull final DidDeleteSensorEvent event) {
    @NonNull final Sensor sensor = event.getSensor();

    if (VirtualSensorHandler.isVirtual(sensor)) {
      Objects.requireNonNull(event.getSensor().getIdentifier());

      @NonNull final VirtualSensorRunner runner = _runners.get(event.getSensor().getIdentifier());
      runner.stop();

      _runners.remove(event.getSensor().getIdentifier());
    }

    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().sensorWasDeleted(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "sensorWasDeleted", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void nodeWillBeCreated (@NonNull final WillCreateNodeEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().nodeWillBeCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "nodeWillBeCreated", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void nodeWasCreated (@NonNull final DidCreateNodeEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().nodeWasCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "nodeWasCreated", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void stateWillBeCreated (@NonNull final WillCreateStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWillBeCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWillBeCreated", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void stateWasCreated (@NonNull final DidCreateStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWasCreated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWasCreated", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void stateWillBeMutated (@NonNull final WillUpdateStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWillBeMutated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWillBeMutated", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void stateWasMutated (@NonNull final DidUpdateStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWasMutated(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWasMutated", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void stateWillBeDeleted (@NonNull final WillDeleteStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWillBeDeleted(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWillBeDeleted", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
    }
  }

  @EventListener
  public void stateWasDeleted (@NonNull final DidDeleteStateEvent event) {
    try {
      for (final VirtualSensorRunner runner : _runners.values()) {
        runner.getHandler().stateWasDeleted(event);
      }
    } catch (@NonNull final Throwable error) {
      getLogger().throwing(getClass().getName(), "stateWasDeleted", error);
      throw new Error(
        "Unable to handle event " + event.toString() + ", an error was raised during " +
        "event processing.", error
      );
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
