package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.repository.LabelStateRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

public class LocalLabelStateRepository
  extends LocalStateRepository<LabelState>
  implements LabelStateRepository
{
  public LocalLabelStateRepository () {
    super(LabelState.class);
  }

  @Override
  public @NonNull Optional<LabelState> findAt (
    @NonNull final ZonedDateTime area, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    return findPrevious(area, sensor);
  }
}
