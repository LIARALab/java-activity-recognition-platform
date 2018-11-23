package org.liara.api.data.repository.database;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.repository.LabelRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
@Primary
public class DatabaseLabelRepository
  extends DatabaseTimeSeriesRepository<LabelState>
  implements LabelRepository
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseLabelRepository (
    @NonNull final EntityManager entityManager
  ) {
    super(entityManager, LabelState.class);

    _entityManager = entityManager;
  }

  @Override
  public Optional<LabelState> findAt (
    @NonNull final ZonedDateTime area,
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    final List<LabelState> result = _entityManager.createQuery(String.join(
      "",
      "SELECT label ",
      "FROM ",
      LabelState.class.getName(),
      " label ",
      "WHERE label._start <= :area ",
      "  AND ( ",
      "       label._end IS NULL ",
      "    OR label._end >= :area ",
      "  ) AND label._sensor._identifier = :sensor"
    ), LabelState.class).setParameter("area", area)
                                                       .setParameter("sensor", sensor.getIdentifier())
                                                       .getResultList();

    return result.size() > 0 ? Optional.ofNullable(result.get(0)) : Optional.empty();
  }

  @Override
  public List<LabelState> findWithDurationGreatherThan (
    @NonNull final ApplicationEntityReference<Sensor> sensor, @NonNull final Duration duration
  )
  {
    final List<LabelState> result = _entityManager.createQuery(String.join(
      "",
      "SELECT label ",
      "FROM ",
      LabelState.class.getName(),
      " label ",
      "WHERE TIMESTAMPDIFF(SECOND, label._start, label._end) > :duration",
      "  AND label._sensor._identifier = :sensor"
    ), LabelState.class)
                                                       .setParameter("duration", duration.getSeconds())
                                                       .setParameter("sensor", sensor.getIdentifier())
                                                       .getResultList();

    return result;
  }
}
