package org.liara.api.recognition.sensor;

import org.liara.api.event.*;
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
  public void sensorWasCreated (@NonNull final SensorEvent event) { }

  @Override
  public void nodeWillBeCreated (@NonNull final NodeWillBeCreatedEvent event) { }

  @Override
  public void nodeWasCreated (@NonNull final NodeEvent event) { }

  @Override
  public void stateWillBeCreated (@NonNull final StateWillBeCreatedEvent event) { }

  @Override
  public void stateWasCreated (@NonNull final StateEvent event) { }

  @Override
  public void stateWillBeMutated (@NonNull final StateWillBeMutatedEvent event) { }

  @Override
  public void stateWasMutated (@NonNull final StateWasMutatedEvent event) { }

  public VirtualSensorRunner getRunner () {
    return _runner;
  }
}
