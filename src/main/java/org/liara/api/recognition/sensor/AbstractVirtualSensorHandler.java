package org.liara.api.recognition.sensor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.event.NodeEvent;
import org.liara.api.event.SensorEvent;
import org.liara.api.event.StateEvent;

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
  public void sensorWillBeCreated (final SensorEvent.@NonNull WillBeCreated event) { }

  @Override
  public void sensorWasCreated (final SensorEvent.@NonNull WasCreated event) { }

  @Override
  public void nodeWillBeCreated (final NodeEvent.@NonNull WillBeCreated event) { }

  @Override
  public void nodeWasCreated (final NodeEvent.@NonNull WasCreated event) { }

  @Override
  public void stateWillBeCreated (final StateEvent.@NonNull WillBeCreated event) { }

  @Override
  public void stateWasCreated (final StateEvent.@NonNull WasCreated event) { }

  @Override
  public void stateWillBeMutated (final StateEvent.@NonNull WillBeMutated event) { }

  @Override
  public void stateWasMutated (final StateEvent.@NonNull WasMutated event) { }

  public @NonNull VirtualSensorRunner getRunner () {
    return _runner;
  }
}
