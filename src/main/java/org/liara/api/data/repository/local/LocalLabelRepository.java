package org.liara.api.data.repository.local;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.repository.LabelRepository;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalLabelRepository
  extends LocalTimeSeriesRepository<LabelState>
  implements LabelRepository
{
  public LocalLabelRepository () {
    super(LabelState.class);
  }

  public static LocalLabelRepository from (@NonNull final LocalEntityManager parent) {
    final LocalLabelRepository result = new LocalLabelRepository();
    parent.addListener(result);
    return result;
  }

  @Override
  public Optional<LabelState> findAt (
    @NonNull final ZonedDateTime area, 
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    return findPrevious(area, sensor);
  }

  @Override
  public List<LabelState> findWithDurationGreatherThan (
    @NonNull final ApplicationEntityReference<Sensor> sensor, @NonNull final Duration duration
  )
  {
    return findAll(sensor).stream().filter(x -> x.getDuration().compareTo(duration) > 0).collect(Collectors.toList());
  }
}
