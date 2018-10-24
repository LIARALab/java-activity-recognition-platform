package org.liara.api.data.repository.database;

import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.TimeSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Primary
public class DatabaseTimeSeriesRepository<TimeState extends State>
       extends DatabaseApplicationEntityRepository<TimeState>
       implements TimeSeriesRepository<TimeState>
{
  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final Class<TimeState> _stateType;
  
  @Autowired
  public DatabaseTimeSeriesRepository (
    @NonNull final EntityManager entityManager,
    @NonNull final Class<TimeState> stateType
  ) {
    super(entityManager, stateType);
    _entityManager = entityManager;
    _stateType = stateType;
  }
  
  @Override
  public List<TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    final int count
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._emittionDate < :date ",
        "   AND state._sensor._identifier = :sensor",
        " ORDER BY state._emittionDate DESC"
      ), _stateType
    ).setParameter("date", date)
     .setParameter("sensor", sensor.getIdentifier())
     .setMaxResults(count)
     .getResultList();
  }
  
  @Override
  public List<TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    final int count
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._emittionDate < :date ",
        "   AND state._sensor._identifier = :sensor ",
        " ORDER BY state._emittionDate ASC"
      ), _stateType
    ).setParameter("date", date)
     .setParameter("sensor", sensor.getIdentifier())
     .setMaxResults(count)
     .getResultList();
  }
  
  @Override
  public List<TimeState> find (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    final int from,
    final int count
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._sensor._identifier = :sensor ",
        " ORDER BY state._emittionDate ASC"
      ), _stateType
    ).setParameter("sensor", sensor.getIdentifier())
     .setMaxResults(count)
     .setFirstResult(from)
     .getResultList();
  }
  
  @Override
  public List<TimeState> findAll (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._sensor._identifier = :sensor ",
        " ORDER BY state._emittionDate ASC"
      ), _stateType
    ).setParameter("sensor", sensor.getIdentifier())
     .getResultList();
  }

  @Override
  public List<TimeState> findWithCorrelation (
    @NonNull final String key,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state._correlations[:key] = :correlated",
        "   AND state._sensor._identifier = :sensor"
      ), _stateType
    ).setParameter("correlated", correlated.getIdentifier())
     .setParameter("sensor", sensor.getIdentifier())
     .setParameter("key", key)
     .getResultList();
  }

  @Override
  public List<TimeState> findWithCorrelations (
    @NonNull final Map<String, ApplicationEntityReference<? extends State>> correlations,
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  ) {
    final CriteriaBuilder builder = _entityManager.getCriteriaBuilder();
    final CriteriaQuery<TimeState> query = builder.createQuery(_stateType);
    
    final Root<TimeState> root = query.from(_stateType);
    final MapJoin<TimeState, String, State> rootCorrelations = root.join(
      root.getModel().getDeclaredMap("correlations", String.class, State.class
      )
    );
    
    final List<Predicate> predicates = new ArrayList<>();
    
    for (final Map.Entry<String, ApplicationEntityReference<? extends State>> correlation : correlations.entrySet()) {
      predicates.add(builder.equal(
        rootCorrelations.on(builder.equal(rootCorrelations.key(), correlation.getKey())).value().get("identifier"),
        correlation.getValue().getIdentifier()
      ));
    }
    
    predicates.add(builder.equal(
      root.get("sensor").get("identifier"),
      sensor.getIdentifier()
    ));
    
    query.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
    
    return _entityManager.createQuery(query).getResultList();
  }

  @Override
  public List<TimeState> findWithAnyCorrelation (
    @NonNull final Collection<String> keys,
    @NonNull final ApplicationEntityReference<? extends State> correlated,
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor
  )
  {
    return _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE KEY(state.orrelations) IN :keys ",
        "   AND state._correlations.identifier = :correlated ",
        "   AND state.sensor.identifier = :sensor "
      ), _stateType
    ).setParameter("correlated", correlated.getIdentifier())
     .setParameter("sensor", sensor.getIdentifier())
     .setParameter("keys", keys)
     .getResultList();
  }

  @Override
  public List<TimeState> findAll (
    @NonNull final Collection<ApplicationEntityReference<? extends Sensor>> sensors
  ) {
    return _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state.sensor.identifier IN :sensors ",
        " ORDER BY state.emittionDate ASC"
      ), _stateType
    ).setParameter(
      "sensors", 
      sensors.stream()
             .map(ApplicationEntityReference::getIdentifier)
             .collect(Collectors.toList())
    ).getResultList();
  }
  
  @Override
  public List<TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final int count
  ) {
    return findAllPreviousQuery(date, inputSensors).setMaxResults(count)
                                                   .getResultList();
  }
  

  @Override
  public List<TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors,
    final int count
  ) {
    return findAllNextQuery(date, inputSensors).setMaxResults(count)
                                               .getResultList();
  }

  @Override
  public List<TimeState> findAllNext (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors
  ) {
    return findAllNextQuery(date, inputSensors).getResultList();
  }
  
  @Override
  public List<TimeState> findAllPrevious (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors
  ) {
    return findAllPreviousQuery(date, inputSensors).getResultList();
  }
  
  private TypedQuery<TimeState> findAllNextQuery (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors
  ) {
    return _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state.emittionDate > :date ",
        "   AND state.sensor.identifier IN :sensors ",
        " ORDER BY state.emittionDate ASC"
      ), _stateType
    ).setParameter("date", date)
     .setParameter(
       "sensors", 
       inputSensors.stream()
                   .map(ApplicationEntityReference::getIdentifier)
                   .collect(Collectors.toSet())
     );
  }

  private TypedQuery<TimeState> findAllPreviousQuery (
    @NonNull final ZonedDateTime date, @NonNull final List<ApplicationEntityReference<? extends Sensor>> inputSensors
  ) {
    return _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ", _stateType.getName(), " state ",
        " WHERE state.emittionDate < :date ",
        "   AND state.sensor.identifier IN :sensors ",
        " ORDER BY state.emittionDate DESC"
      ), _stateType
    ).setParameter("date", date)
     .setParameter(
       "sensors", 
       inputSensors.stream()
                   .map(ApplicationEntityReference::getIdentifier)
                   .collect(Collectors.toSet())
     );
  }
}
