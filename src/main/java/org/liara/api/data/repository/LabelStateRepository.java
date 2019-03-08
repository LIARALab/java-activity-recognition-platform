package org.liara.api.data.repository;

import org.liara.api.data.entity.state.LabelState;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface LabelStateRepository
  extends StateRepository<LabelState>
{
  @NonNull
  Optional<LabelState> findAt (
    @NonNull final ZonedDateTime area,
    @NonNull final Long sensorIdentifier
  );
}
