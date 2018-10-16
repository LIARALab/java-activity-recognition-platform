package org.liara.api.data.repository.local;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.repository.ActivationsRepository;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalActivationsRepository
       extends LocalTimeSeriesRepository<ActivationState>
       implements ActivationsRepository
{
  public static LocalActivationsRepository from (@NonNull final LocalEntityManager parent) {
    final LocalActivationsRepository result = new LocalActivationsRepository();
    parent.addListener(result);
    return result;
  }
  
  public LocalActivationsRepository() {
    super(ActivationState.class);
  }

  @Override
  public Optional<ActivationState> findAt (
    @NonNull final ZonedDateTime area, 
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    return findPrevious(area, sensor);
  }

  @Override
  public List<ActivationState> findWithDurationGreatherThan (
    @NonNull final ApplicationEntityReference<Sensor> sensor, @NonNull final Duration duration
  )
  {
    return findAll(sensor).stream().filter(x -> x.getDuration().compareTo(duration) > 0).collect(Collectors.toList());
  }
}
