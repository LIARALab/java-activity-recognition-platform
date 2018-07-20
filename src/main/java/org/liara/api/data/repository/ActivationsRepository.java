package org.liara.api.data.repository;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.springframework.lang.NonNull;

public interface ActivationsRepository extends TimeSeriesRepository<ActivationState>
{
  public Optional<ActivationState> findAt (
    @NonNull final ZonedDateTime area,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  );
}
