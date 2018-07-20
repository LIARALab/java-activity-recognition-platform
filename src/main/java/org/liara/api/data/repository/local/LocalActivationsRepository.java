package org.liara.api.data.repository.local;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.repository.ActivationsRepository;
import org.springframework.lang.NonNull;

public class LocalActivationsRepository
       extends LocalTimeSeriesRepository<ActivationState>
       implements ActivationsRepository
{
  public LocalActivationsRepository() {
    super(ActivationState.class);
  }
  
  public LocalActivationsRepository (@NonNull final LocalEntityManager parent) {
    super(parent, ActivationState.class);
  }

  @Override
  public Optional<ActivationState> findAt (
    @NonNull final ZonedDateTime area, 
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    return findPrevious(area, sensor);
  }
}
