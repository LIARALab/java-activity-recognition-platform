package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
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
    @NonNull final ZonedDateTime area, @NonNull final Long sensorIdentifier
  ) {
    return findPrevious(
      area,
      sensorIdentifier
    );
  }
}
