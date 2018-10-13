package org.liara.api.data.repository;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.ActivationState;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface ActivationsRepository extends TimeSeriesRepository<ActivationState>
{
  public Optional<ActivationState> findAt (
    @NonNull final ZonedDateTime area,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  );
}
