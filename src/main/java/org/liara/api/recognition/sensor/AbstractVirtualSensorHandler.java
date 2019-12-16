package org.liara.api.recognition.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.event.node.DidCreateNodeEvent;
import org.liara.api.event.node.WillCreateNodeEvent;
import org.liara.api.event.sensor.DidCreateSensorEvent;
import org.liara.api.event.sensor.WillCreateSensorEvent;
import org.liara.api.event.state.DidCreateStateEvent;
import org.liara.api.event.state.DidUpdateStateEvent;
import org.liara.api.event.state.WillCreateStateEvent;
import org.liara.api.event.state.WillUpdateStateEvent;

import java.util.Optional;

public abstract class AbstractVirtualSensorHandler implements VirtualSensorHandler
{
  @Nullable
  private VirtualSensorRunner _runner = null;

  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    _runner = runner;
  }

  @Override
  public void resume (@NonNull final VirtualSensorRunner runner) {
    _runner = runner;
  }

  @Override
  public void stop () {
    _runner = null;
  }

  @Override
  public void pause () {
    _runner = null;
  }

  @Override
  public void sensorWillBeCreated (final WillCreateSensorEvent event) { }

  @Override
  public void sensorWasCreated (final DidCreateSensorEvent event) { }

  @Override
  public void nodeWillBeCreated (final WillCreateNodeEvent event) { }

  @Override
  public void nodeWasCreated (final DidCreateNodeEvent event) { }

  @Override
  public void stateWillBeCreated (final WillCreateStateEvent event) { }

  @Override
  public void stateWasCreated (final DidCreateStateEvent event) { }

  @Override
  public void stateWillBeMutated (final WillUpdateStateEvent event) { }

  @Override
  public void stateWasMutated (final DidUpdateStateEvent event) { }

  public @NonNull Optional<VirtualSensorRunner> getRunner () {
    return Optional.ofNullable(_runner);
  }

  public @NonNull Optional<Sensor> getSensor () {
    return getRunner().map(VirtualSensorRunner::getSensor);
  }

  public <T extends SensorConfiguration> @NonNull Optional<T> getConfiguration (
    @NonNull final Class<T> clazz
  ) {
    return getSensor().map(Sensor::getConfiguration).map(x -> x.as(clazz));
  }
}
