package org.liara.recognition.sensor;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class AbstractVirtualSensorHandler implements VirtualSensorHandler
{
  @Nullable
  private VirtualSensorRunner _runner = null;

  @Override
  public void afterCreation (@NonNull final VirtualSensorRunner runner) {
    _runner = runner;
  }

  @Override
  public void afterRestart (@NonNull final VirtualSensorRunner runner) {
    _runner = runner;
  }

  @Override
  public void beforeStop () {
    _runner = null;
  }

  @Override
  public void beforeDeletion () {
    _runner = null;
  }

  public VirtualSensorRunner getRunner () {
    return _runner;
  }
}
