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
import org.springframework.lang.Nullable;

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
  public void sensorWillBeCreated (@NonNull final SensorWillBeCreatedEvent event) { }

  @Override
  public void sensorWasCreated (@NonNull final SensorWasCreatedEvent event) { }

  @Override
  public void nodeWillBeCreated (@NonNull final NodeWillBeCreatedEvent event) { }

  @Override
  public void nodeWasCreated (@NonNull final NodeWasCreatedEvent event) { }

  @Override
  public void stateWillBeCreated (@NonNull final StateWillBeCreatedEvent event) { }

  @Override
  public void stateWasCreated (@NonNull final StateWasCreatedEvent event) { }

  @Override
  public void stateWillBeMutated (@NonNull final StateWillBeMutatedEvent event) { }

  @Override
  public void stateWasMutated (@NonNull final StateWasMutatedEvent event) { }

  public VirtualSensorRunner getRunner () {
    return _runner;
  }
}
