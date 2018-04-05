package org.liara.sensor;

import org.liara.api.data.entity.State;
import org.springframework.lang.NonNull;

public interface SensorHandler
{
  public void onCreation ();
  public void onStateAddition (@NonNull final State state);
  public void onStateDeletion (@NonNull final State state);
  public void onStateUpdate (@NonNull final State state);
  public void onDestroy ();
}
