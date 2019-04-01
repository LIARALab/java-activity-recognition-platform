package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.api.io.WritingSession;
import org.liara.collection.operator.cursoring.Cursor;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

public class DatabaseValueStateRepository<Value, Wrapper extends ValueState>
  extends DatabaseStateRepository<Wrapper>
  implements ValueStateRepository<Value, Wrapper>
{
  @NonNull
  private final WritingSession _writingSession;

  public DatabaseValueStateRepository (
    @NonNull final WritingSession writingSession,
    @NonNull final Class<Wrapper> type
  ) {
    super(writingSession, type);
    _writingSession = writingSession;
  }

  @Override
  public @NonNull List<@NonNull Wrapper> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Wrapper> states = _writingSession.getEntityManager().createQuery(
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
    states.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

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
    @NonNull final TypedQuery<Wrapper> states = _writingSession.getEntityManager().createQuery(
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
    states.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) states.setMaxResults(cursor.getLimit());

    return states.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Wrapper> findAllWithValue (
    @NonNull final List<@NonNull Long> inputSensorIdentifiers,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Wrapper> states = _writingSession.getEntityManager().createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state " +
      " WHERE state.sensorIdentifier IN :sensors " +
      "   AND state.value = :value" +
      " ORDER BY state.emissionDate ASC",
      getManagedEntity()
    );

    states.setParameter("sensors", inputSensorIdentifiers);
    states.setParameter("value", value);
    states.setFirstResult(cursor.getOffset());
    states.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) states.setMaxResults(cursor.getLimit());

    return states.getResultList();
  }
}
