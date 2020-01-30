package org.liara.api.data.repository.database;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.StateRepository;
import org.liara.collection.operator.cursoring.Cursor;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DatabaseStateRepository<TimeState extends State>
  extends DatabaseApplicationEntityRepository<TimeState>
  implements StateRepository<TimeState>
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseStateRepository (
    @NonNull final EntityManager entityManager,
    @NonNull final Class<TimeState> stateType
  ) {
    super(entityManager, stateType);
    _entityManager = entityManager;
  }

  @Override
  public @NonNull Optional<TimeState> find (
          @NonNull final Long sensorIdentifier,
          @NonNull final ZonedDateTime date
  ) {
    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(
            "SELECT state FROM " + getManagedEntity().getName() + " state" +
                    " WHERE state.sensorIdentifier = :sensorIdentifier" +
                    "   AND state.emissionDate = :date",
            getManagedEntity()
    );

    query.setParameter("date", date);
    query.setParameter("sensorIdentifier", sensorIdentifier);

    @NonNull final List<@NonNull TimeState> result = query.getResultList();

    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }

  @Override
  public @NonNull List<@NonNull TimeState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  ) {
    if (sensorIdentifiers.isEmpty()) {
      return Collections.emptyList();
    }

    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state" +
      " WHERE state.sensorIdentifier IN :sensorIdentifiers" +
      "   AND state.emissionDate < :date" +
      " ORDER BY state.emissionDate DESC, state.identifier DESC",
      getManagedEntity()
    );

    query.setParameter("date", date);
    query.setParameter("sensorIdentifiers", sensorIdentifiers);
    query.setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull TimeState> findNext (
    @NonNull final ZonedDateTime date,
    @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  ) {
    if (sensorIdentifiers.isEmpty()) {
      return Collections.emptyList();
    }

    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state" +
      " WHERE state.sensorIdentifier IN :sensorIdentifiers" +
      "   AND state.emissionDate > :date" +
      " ORDER BY state.emissionDate ASC, state.identifier ASC",
      getManagedEntity()
    );

    query.setParameter("date", date);
    query.setParameter("sensorIdentifiers", sensorIdentifiers);
    query.setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull TimeState> find (
    @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  ) {
    if (sensorIdentifiers.isEmpty()) {
      return Collections.emptyList();
    }

    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state" +
      " WHERE state.sensorIdentifier IN :sensorIdentifiers" +
      " ORDER BY state.emissionDate ASC, state.identifier ASC",
      getManagedEntity()
    );

    query.setParameter("sensorIdentifiers", sensorIdentifiers);
    query.setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull @NonNegative Long count (@NonNull final Collection<Long> sensorIdentifiers) {
    if (sensorIdentifiers.isEmpty()) {
      return 0L;
    }

    @NonNull final TypedQuery<Long> query = _entityManager.createQuery(
      "SELECT COUNT(*) FROM " + getManagedEntity().getName() + " state" +
      " WHERE state.sensorIdentifier IN :sensorIdentifiers",
      Long.class
    );

    query.setParameter("sensorIdentifiers", sensorIdentifiers);

    return query.getSingleResult();
  }

  @Override
  public @NonNull Optional<TimeState> findLast (
    @NonNull final Collection<@NonNull Long> sensors
  ) {
    if (sensors.isEmpty()) {
      return Optional.empty();
    }

    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state" +
      " WHERE state.sensorIdentifier IN :sensors" +
      " ORDER BY state.emissionDate DESC, state.identifier DESC",
      getManagedEntity()
    );

    query.setParameter("sensor", sensors);
    query.setFirstResult(0);
    query.setMaxResults(1);

    @NonNull final List<@NonNull TimeState> result = query.getResultList();

    return (result.size() > 0) ? Optional.of(result.get(0)) : Optional.empty();
  }
}
