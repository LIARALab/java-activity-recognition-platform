package org.liara.api.data.repository.database;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.repository.ActivationsRepository;
import org.springframework.lang.NonNull;

public class DatabaseActivationRepository
       extends DatabaseTimeSeriesRepository<ActivationState>
       implements ActivationsRepository
{
  @NonNull
  private final EntityManager _entityManager;
  
  public DatabaseActivationRepository(
    @NonNull final EntityManager entityManager
  ) {
    super(ActivationState.class, entityManager);
    
    _entityManager = entityManager;
  }

  @Override
  public Optional<ActivationState> at (
    @NonNull final ZonedDateTime area, 
    @NonNull final ApplicationEntityReference<Sensor> sensor
  ) {
    final List<ActivationState> result = _entityManager.createQuery(
      String.join(
        "", 
        "SELECT activation ",
        "FROM ", ActivationState.class.getName(), " activation ",
        "WHERE activation._start <= :area ",
        "  AND ( ",
        "       activation._end IS NULL ",
        "    OR activation._end >= :area ",
        "  ) AND activation._sensor._identifier = :sensor"
      ), ActivationState.class
    ).setParameter("area", area)
     .setParameter("sensor", sensor.getIdentifier())
     .getResultList();
    
    return result.size() > 0 ? Optional.ofNullable(result.get(0))
                             : Optional.empty();
  }
}
