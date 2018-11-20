package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.repository.LabelStateRepository;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
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
    return findPrevious(area, sensorIdentifier);
  }
}
