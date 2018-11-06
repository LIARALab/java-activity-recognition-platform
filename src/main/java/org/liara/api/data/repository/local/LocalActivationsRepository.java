package org.liara.api.data.repository.local;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.repository.ActivationsRepository;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Optional;

public class LocalActivationsRepository
  extends LocalStateRepository<ActivationState>
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
    @NonNull final ZonedDateTime area, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    return findPrevious(area, sensor);
  }
}
