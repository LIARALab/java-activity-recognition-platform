package org.liara.api.data.repository.database;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.repository.ActivationsRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
@Primary
public class DatabaseActivationRepository
  extends DatabaseStateRepository<ActivationState>
  implements ActivationsRepository
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseActivationRepository(
    @NonNull final EntityManager entityManager
  ) {
    super(entityManager, ActivationState.class);

    _entityManager = entityManager;
  }

  @Override
  public Optional<ActivationState> findAt (
    @NonNull final ZonedDateTime area, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    final List<ActivationState> result = _entityManager.createQuery(String.join(
      "",
      "SELECT activation ",
      "FROM ", ActivationState.class.getName(), " activation ",
      "WHERE activation.start <= :area ",
      "  AND ( ",
      "       activation.end IS NULL ",
      "    OR activation.end >= :area ",
      "  ) AND activation.sensor.identifier = :sensor"
    ), ActivationState.class).setParameter("area", area)
                                                       .setParameter("sensor", sensor.getIdentifier())
                                                       .getResultList();

    return result.size() > 0 ? Optional.ofNullable(result.get(0)) : Optional.empty();
  }
}
