package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
import org.liara.api.data.entity.state.Correlation;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseCorrelationRepository
  extends DatabaseApplicationEntityRepository<Correlation>
  implements CorrelationRepository
{
  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseCorrelationRepository (
    @Qualifier("generatorEntityManager") @NonNull final EntityManager entityManager
  ) {
    super(entityManager, Correlation.class);
    _entityManager = entityManager;
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsOf (
    @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery(
      "SELECT correlation FROM " + getManagedEntity().getName() + " correlation" +
      " WHERE correlation.startStateIdentifier = :stateIdentifier" +
      "    OR correlation.endStateIdentifier = :stateIdentifier" +
      " ORDER BY correlation.identifier",
      getManagedEntity()
    );

    query.setParameter("stateIdentifier", stateIdentifier);
    query.setFirstResult(cursor.getOffset());
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsThatStartBy (
    @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery(
      "SELECT correlation FROM " + getManagedEntity().getName() + " correlation " +
      " WHERE correlation.startStateIdentifier = :stateIdentifier " +
      " ORDER BY correlation.identifier",
      getManagedEntity()
    );

    query.setParameter("stateIdentifier", stateIdentifier);
    query.setFirstResult(cursor.getOffset());
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatStartBy (
    @NonNull final String name, @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery(
      "SELECT correlation FROM " + getManagedEntity().getName() + " correlation" +
      " WHERE correlation.startStateIdentifier = :stateIdentifier" +
      "   AND correlation.name = :name" +
      " ORDER BY correlation.identifier",
      getManagedEntity()
    );

    query.setParameter("stateIdentifier", stateIdentifier);
    query.setParameter("name", name);
    query.setFirstResult(cursor.getOffset());
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatStartBy (
    @NonNull final Long sensorIdentifier,
    @NonNull final String name, @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery(
      "SELECT correlation FROM " + getManagedEntity().getName() + " correlation" +
      " INNER JOIN " + State.class.getName() + " startState" +
      "         ON correlation.startStateIdentifier = startState.identifier" +
      " WHERE correlation.startStateIdentifier = :stateIdentifier" +
      "   AND correlation.name = :name " +
      "   AND startState.sensorIdentifier = :sensorIdentifier" +
      " ORDER BY correlation.identifier",
      getManagedEntity()
    );

    query.setParameter("stateIdentifier", stateIdentifier);
    query.setParameter("name", name);
    query.setParameter("sensorIdentifier", sensorIdentifier);
    query.setFirstResult(cursor.getOffset());
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsThatEndsBy (
    @NonNull final Long state,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery(
      "SELECT correlation FROM " + getManagedEntity().getName() + " correlation" +
      " WHERE correlation.endStateIdentifier = :state" +
      " ORDER BY correlation.identifier ASC",
      getManagedEntity()
    );

    query.setParameter("state", state);
    query.setFirstResult(cursor.getOffset());
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsWithNameAndThatEndsBy (
    @NonNull final String name,
    @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery(
      "SELECT correlation FROM " + getManagedEntity().getName() + " correlation" +
      " WHERE correlation.endStateIdentifier = :stateIdentifier" +
      "   AND correlation.name = :name" +
      " ORDER BY correlation.identifier",
      getManagedEntity()
    );

    query.setParameter("stateIdentifier", stateIdentifier);
    query.setParameter("name", name);
    query.setFirstResult(cursor.getOffset());
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Correlation> findCorrelationsFromSeriesWithNameAndThatEndsBy (
    @NonNull final Long sensorIdentifier,
    @NonNull final String name,
    @NonNull final Long stateIdentifier,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<Correlation> query = _entityManager.createQuery(
      "SELECT correlation FROM " + getManagedEntity().getName() + " correlation" +
      " INNER JOIN " + State.class.getName() + " startState " +
      "         ON correlation.startStateIdentifier = startState.identifier " +
      " WHERE correlation.endStateIdentifier = :stateIdentifier " +
      "   AND correlation.name = :name " +
      "   AND startState.sensorIdentifier = :sensorIdentifier" +
      " ORDER BY correlation.identifier",
      getManagedEntity()
    );

    query.setParameter("stateIdentifier", stateIdentifier);
    query.setParameter("name", name);
    query.setParameter("sensorIdentifier", sensorIdentifier);
    query.setFirstResult(cursor.getOffset());
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }
}
