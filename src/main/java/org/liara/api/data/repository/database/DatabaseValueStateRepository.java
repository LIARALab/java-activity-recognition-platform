package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.collection.operator.cursoring.Cursor;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

public class DatabaseValueStateRepository<Value, Wrapper extends ValueState>
  extends DatabaseStateRepository<Wrapper>
  implements ValueStateRepository<Value, Wrapper>
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseValueStateRepository (
    @NonNull final EntityManager entityManager,
    @NonNull final Class<Wrapper> type
  ) {
    super(entityManager, type);
    _entityManager = entityManager;
  }

  @Override
  public @NonNull List<@NonNull Wrapper> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Wrapper> states = _entityManager.createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state " +
      " WHERE stat.emissionDate < :date " +
      "   AND state.sensorIdentifier IN :sensors " +
      "   AND state.value = :value" +
      " ORDER BY state.emissionDate DESC, state.identifier DESC",
      getManagedEntity()
    );

    states.setParameter("date", date);
    states.setParameter("sensors", inputSensorIdentifiers);
    states.setParameter("value", value);
    states.setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) states.setMaxResults(cursor.getLimit());

    return states.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Wrapper> findNextWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Wrapper> states = _entityManager.createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state " +
      " WHERE state.emissionDate > :date " +
      "   AND state.sensorIdentifier IN :sensors " +
      "   AND state.value = :value" +
      " ORDER BY state.emissionDate ASC",
      getManagedEntity()
    );

    states.setParameter("date", date);
    states.setParameter("sensors", inputSensorIdentifiers);
    states.setParameter("value", value);
    states.setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) states.setMaxResults(cursor.getLimit());

    return states.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Wrapper> findAllWithValue (
    @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Wrapper> states = _entityManager.createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state " +
      " WHERE state.sensorIdentifier IN :sensors " +
      "   AND state.value = :value" +
      " ORDER BY state.emissionDate ASC",
      getManagedEntity()
    );

    states.setParameter("sensors", inputSensorIdentifiers);
    states.setParameter("value", value);
    states.setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) states.setMaxResults(cursor.getLimit());

    return states.getResultList();
  }
}
