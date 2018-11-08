package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.repository.LabelStateRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
@Primary
public class DatabaseLabelStateRepository
  extends DatabaseStateRepository<LabelState>
  implements LabelStateRepository
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseLabelStateRepository (
    @NonNull final EntityManager entityManager
  ) {
    super(entityManager, LabelState.class);

    _entityManager = entityManager;
  }

  @Override
  public @NonNull Optional<LabelState> findAt (
    @NonNull final ZonedDateTime area, @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    @NonNull final List<@NonNull LabelState> result = _entityManager.createQuery(String.join(
      "", "SELECT label ", "FROM ", getManagedEntity().getName(), " label ", "WHERE label.start <= :area ",
      "  AND ( ", "       label.end IS NULL ", "    OR label.end >= :area ", "  ) AND label.sensorIdentifier = :sensor"
    ), LabelState.class).setParameter("area", area).setParameter("sensor", sensor).setMaxResults(1).getResultList();

    return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
  }
}
