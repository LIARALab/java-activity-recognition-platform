package org.liara.api.data.repository.database;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.repository.BooleanStateRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Primary
public class DatabaseBooleanStateRepository
       extends DatabaseTimeSeriesRepository<BooleanState>
       implements BooleanStateRepository
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseBooleanStateRepository(@NonNull final EntityManager entityManager) {
    super(entityManager, BooleanState.class);
    _entityManager = entityManager;
  }

  @Override
  public List<BooleanState> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", BooleanState.class.getName(), " state ",
        " WHERE state._emittionDate < :date ",
        "   AND state._sensor._identifier IN :sensors ",
        "   AND state._value = :value",
        " ORDER BY state._emittionDate DESC"
      ), BooleanState.class
    ).setParameter("date", date)
     .setParameter(
       "sensors", 
       inputSensors.stream()
                   .map(ApplicationEntityReference::getIdentifier)
                   .collect(Collectors.toSet())
     )
     .setParameter("value", value)
     .setMaxResults(count)
     .getResultList();
  }

  @Override
  public List<BooleanState> findNextWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", BooleanState.class.getName(), " state ",
        " WHERE state._emittionDate > :date ",
        "   AND state._sensor._identifier IN :sensors ",
        "   AND state._value = :value",
        " ORDER BY state._emittionDate ASC"
      ), BooleanState.class
    ).setParameter("date", date)
     .setParameter(
       "sensors", 
       inputSensors.stream()
                   .map(ApplicationEntityReference::getIdentifier)
                   .collect(Collectors.toSet())
     )
     .setParameter("value", value)
     .setMaxResults(count)
     .getResultList();
  }

  @Override
  public List<BooleanState> findAllWithValue (
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors, 
    @NonNull final boolean value
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", BooleanState.class.getName(), " state ",
        " WHERE state._sensor._identifier IN :sensors ",
        "   AND state._value = :value",
        " ORDER BY state._emittionDate ASC"
      ), BooleanState.class
    ).setParameter(
       "sensors", 
       inputSensors.stream()
                   .map(ApplicationEntityReference::getIdentifier)
                   .collect(Collectors.toSet())
     )
     .setParameter("value", value)
     .getResultList();
  }

}