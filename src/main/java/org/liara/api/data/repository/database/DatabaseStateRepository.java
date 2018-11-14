package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.StateRepository;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
@Primary
public class DatabaseStateRepository<TimeState extends State>
       extends DatabaseApplicationEntityRepository<TimeState>
  implements StateRepository<TimeState>
{
  @NonNull
  private final EntityManager _entityManager;
  
  @Autowired
  public DatabaseStateRepository (
    @NonNull final EntityManager entityManager,
    @NonNull final Class<TimeState> stateType
  ) {
    super(entityManager, stateType);
    _entityManager = entityManager;
  }
  
  @Override
  public @NonNull List<@NonNull TimeState> findPrevious (
    @NonNull final ZonedDateTime date, @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ",
        getManagedEntity().getName(),
        " state ",
        " WHERE state.emissionDate < :date ",
        "   AND state.sensorIdentifier IN :sensorIdentifiers",
        " ORDER BY state.emissionDate DESC, state.identifier DESC"
      ), getManagedEntity())
                                                               .setParameter("date", date)
                                                   .setParameter(
                                                     "sensorIdentifiers",
                                                     sensorIdentifiers
                                                   )
                                                               .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }
  
  @Override
  public @NonNull List<@NonNull TimeState> findNext (
    @NonNull final ZonedDateTime date, @NonNull final Collection<@NonNull Long> sensorIdentifiers,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ",
        getManagedEntity().getName(),
        " state ",
        " WHERE state.emissionDate < :date ",
        "   AND state.sensorIdentifier IN :sensorIdentifiers ",
        " ORDER BY state.emissionDate ASC, state.identifier ASC"
      ), getManagedEntity())
                                                               .setParameter("date", date)
                                                   .setParameter(
                                                     "sensorIdentifiers",
                                                     sensorIdentifiers
                                                   )
                                                               .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull TimeState> find (
    @NonNull final Collection<@NonNull Long> sensorIdentifiers, @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ", "  FROM ", getManagedEntity().getName(), " state ",
        " WHERE state.sensorIdentifier IN :sensorIdentifiers ",
        " ORDER BY state.emissionDate ASC, state.identifier ASC"
      ),
      getManagedEntity()
    )
                                                   .setParameter(
                                                     "sensorIdentifiers",
                                                     sensorIdentifiers
                                                   )
                                                   .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull Optional<TimeState> findLast (
    @NonNull final Collection<@NonNull Long> sensors
  ) {
    @NonNull final TypedQuery<TimeState> query = _entityManager.createQuery(String.join(
      "",
      "SELECT state ",
      "  FROM ",
      getManagedEntity().getName(),
      " state ", " WHERE state.sensorIdentifier IN :sensors",
      " ORDER BY state.emissionDate DESC, state.identifier DESC"
    ), getManagedEntity()).setParameter("sensor", sensors).setFirstResult(0).setMaxResults(1);

    @NonNull final List<@NonNull TimeState> result = query.getResultList();

    return (result.size() > 0) ? Optional.of(result.get(0)) : Optional.empty();
  }

}
