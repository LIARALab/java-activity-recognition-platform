package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.StateRepository;
import org.liara.api.io.WritingSession;
import org.liara.collection.operator.cursoring.Cursor;

import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DatabaseStateRepository<TimeState extends State>
  extends DatabaseApplicationEntityRepository<TimeState>
  implements StateRepository<TimeState>
{
  @NonNull
  private final WritingSession _writingSession;

  public DatabaseStateRepository (
    @NonNull final WritingSession writingSession,
    @NonNull final Class<TimeState> stateType
  ) {
    super(writingSession, stateType);
    _writingSession = writingSession;
  }

  @Override
  public @NonNull List<@NonNull TimeState> findPrevious (
    @NonNull final ZonedDateTime date,
    @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<TimeState> query = _writingSession.getEntityManager().createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state" +
      " WHERE state.emissionDate < :date " +
      "   AND state.sensorIdentifier IN :sensorIdentifiers" +
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
    @NonNull final TypedQuery<TimeState> query = _writingSession.getEntityManager().createQuery(
      "SELECT state FROM " + getManagedEntity().getName() + " state" +
      " WHERE state.emissionDate > :date" +
      "   AND state.sensorIdentifier IN :sensorIdentifiers" +
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
    @NonNull final TypedQuery<TimeState> query = _writingSession.getEntityManager().createQuery(
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
  public @NonNull Optional<TimeState> findLast (
    @NonNull final Collection<@NonNull Long> sensors
  ) {
    @NonNull final TypedQuery<TimeState> query = _writingSession.getEntityManager().createQuery(
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
