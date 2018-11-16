package org.liara.api.data.repository.database;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.repository.BooleanStateRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    @NonNull final BooleanState created,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  )
  {
    return _entityManager
             .createQuery(
               String.join("",
                           "SELECT state ",
                           "  FROM ",
                           BooleanState.class.getName(),
                           " state ",
                           " WHERE (state._emittionDate < :date OR ",
                           "   (state._emittionDate = :date AND state._identifier < :identifier))",
                           "   AND state._sensor._identifier IN :sensors ",
                           "   AND state._value = :value",
                           " ORDER BY state._emittionDate DESC, state._identifier DESC"
               ),
               BooleanState.class
             )
             .setParameter(
               "date",
               created.getEmittionDate()
             )
             .setParameter(
               "identifier",
               created.getIdentifier()
             )
             .setParameter(
               "sensors",
               inputSensors.stream().map(ApplicationEntityReference::getIdentifier).collect(Collectors.toSet())
             )
             .setParameter(
               "value",
               value
             )
             .setMaxResults(count)
             .getResultList();
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
        " ORDER BY state._emittionDate DESC, state._identifier DESC"
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
    @NonNull final BooleanState created,
    @NonNull final List<ApplicationEntityReference<Sensor>> inputSensors,
    final boolean value,
    final int count
  )
  {
    return _entityManager
             .createQuery(
               String.join("",
                           "SELECT state ",
                           "  FROM ",
                           BooleanState.class.getName(),
                           " state ",
                           " WHERE (state._emittionDate > :date OR ",
                           "   (state._emittionDate = :date AND state._identifier > :identifier))",
                           "   AND state._sensor._identifier IN :sensors ",
                           "   AND state._value = :value",
                           " ORDER BY state._emittionDate ASC, state._identifier ASC"
               ),
               BooleanState.class
             )
             .setParameter(
               "date",
               created.getEmittionDate()
             )
             .setParameter(
               "identifier",
               created.getIdentifier()
             )
             .setParameter(
               "sensors",
               inputSensors.stream().map(ApplicationEntityReference::getIdentifier).collect(Collectors.toSet())
             )
             .setParameter(
               "value",
               value
             )
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
        " ORDER BY state._emittionDate ASC, state._identifier ASC"
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
        " ORDER BY state._emittionDate ASC, state._identifier ASC"
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
