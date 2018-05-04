package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;

import org.liara.api.data.entity.ApplicationEntitySnapshot;
import org.springframework.lang.NonNull;

public class StateSnapshot extends ApplicationEntitySnapshot
{
  @NonNull
  private final Long _sensor;
  
  @NonNull
  private final ZonedDateTime _emittionDate;
  
  public StateSnapshot (@NonNull final StateSnapshot toCopy) {
    super(toCopy);
    
    _sensor = toCopy.getSensor();
    _emittionDate = toCopy.getEmittionDate();
  }

  public StateSnapshot (@NonNull final State model) {
    super(model);
    
    _sensor = model.getSensorIdentifier();
    _emittionDate = model.getEmittionDate();
  }
  
  public Long getSensor () {
    return _sensor;
  }
  
  public ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }
  
  @Override
  public StateSnapshot clone () {
    return new StateSnapshot(this);
  }
  
  @Override
  public State getModel () {
    return (State) super.getModel();
  }
}
