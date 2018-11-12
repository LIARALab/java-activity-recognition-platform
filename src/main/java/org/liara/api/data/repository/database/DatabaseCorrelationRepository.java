package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class DatabaseCorrelationRepository
  extends DatabaseApplicationEntityRepository<Correlation>
  implements CorrelationRepository
{
  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseCorrelationRepository (@NonNull final EntityManager entityManager) {
    super(entityManager, Correlation.class);
    _entityManager = entityManager;
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsOf (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Correlation> query =
      _entityManager.createQuery("SELECT correlation FROM " + getManagedEntity().getName() + " correlation " +
                                                                              "WHERE correlation.startStateIdentifier" +
                                 " = :state OR correlation.endStateIdentifier = :state " +
                                                                              "ORDER BY correlation.identifier",
                                                                              getManagedEntity()
    )
                                                                 .setParameter("state", state)
                                                                 .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsThatStartBy (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Correlation> query =
      _entityManager.createQuery("SELECT correlation FROM " + getManagedEntity().getName() + " correlation " +
                                                                              "WHERE correlation.startStateIdentifier" +
                                 " = :state " + "ORDER BY correlation.identifier",
                                                                              getManagedEntity()
    )
                                                                 .setParameter("state", state)
                                                                 .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatStartBy (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Correlation> query =
      _entityManager.createQuery("SELECT correlation FROM " + getManagedEntity().getName() + " correlation " +
                                                                              "WHERE correlation.startStateIdentifier" +
                                 " = :state AND correlation.name = :name " +
                                                                              "ORDER BY correlation.identifier",
                                                                              getManagedEntity()
    )
                                                                 .setParameter("state", state)
                                                                 .setParameter("name", name)
                                                                 .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatStartBy (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Correlation> query =
      _entityManager.createQuery("SELECT correlation FROM " + getManagedEntity().getName() + " correlation " + "INNER" +
                                 " JOIN " +
                                                                              State.class.getName() + " state ON " +
                                 "correlation.startStateIdentifier = state.identifier " +
                                                                              "WHERE correlation.startStateIdentifier" +
                                 " = :state " + "  AND correlation.name = :name " +
                                                                              "  AND state.sensorIdentifier = :sensor" +
                                 " " + "ORDER BY correlation.identifier",
                                                                              getManagedEntity()
    )
                                                                 .setParameter("state", state)
                                                                 .setParameter("name", name)
                                                                 .setParameter("sensor", sensor)
                                                                 .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsThatEndsBy (
    @NonNull final ApplicationEntityReference<? extends State> state, @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Correlation> query =
      _entityManager.createQuery("SELECT correlation FROM " + getManagedEntity().getName() + " correlation " +
                                                                              "WHERE correlation.endStateIdentifier =" +
                                 " :state " + "ORDER BY correlation.identifier",
                                                                              getManagedEntity()
    )
                                                                 .setParameter("state", state)
                                                                 .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatEndsBy (
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery(
      "SELECT correlation FROM " + getManagedEntity().getName() + " correlation " +
      "WHERE correlation.endStateIdentifier = :state AND correlation.name = :name " + "ORDER BY correlation.identifier",
      getManagedEntity()
    ).setParameter("state", state).setParameter("name", name).setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatEndsBy (
    @NonNull final ApplicationEntityReference<? extends Sensor> sensor,
    @NonNull final String name,
    @NonNull final ApplicationEntityReference<? extends State> state,
    @NonNull final Cursor cursor
  )
  {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery("SELECT correlation FROM " + getManagedEntity().getName() + " correlation " + "INNER JOIN " +
                                                                              State.class.getName() + " state ON correlation.startStateIdentifier = state.identifier " +
                                                                              "WHERE correlation.endStateIdentifier = :state " + "  AND correlation.name = :name " +
                                                                              "  AND state.sensorIdentifier = :sensor " + "ORDER BY correlation.identifier",
                                                                              getManagedEntity()
    )
                                                                 .setParameter("state", state)
                                                                 .setParameter("name", name)
                                                                 .setParameter("sensor", sensor)
                                                                 .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }
}
