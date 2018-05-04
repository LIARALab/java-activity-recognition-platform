package org.liara.api.recognition.sensor;

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

  public VirtualSensorRunner getRunner () {
    return _runner;
  }
}
