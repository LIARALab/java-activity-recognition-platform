package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.ValueState;
import org.liara.api.data.repository.ValueStateRepository;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@Scope("prototype")
@Primary
public class DatabaseValueStateRepository<Value>
  extends DatabaseStateRepository<ValueState<Value>>
  implements ValueStateRepository<Value>
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseValueStateRepository (
    @NonNull final EntityManager entityManager, @NonNull final Class<ValueState<Value>> type
  )
  {
    super(entityManager, type);
    _entityManager = entityManager;
  }

  @Override
  public @NonNull List<@NonNull ValueState<Value>> findPreviousWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<ValueState<Value>> states = _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ",
        getManagedEntity().getName(),
        " state ",
        " WHERE state.emissionDate < :date ",
        "   AND state.sensorIdentifier IN :sensors ",
        "   AND state.value = :value",
        " ORDER BY state.emissionDate DESC"
      ), getManagedEntity()
    ).setParameter("date", date)
                                                                        .setParameter("sensors", inputSensors)
                                                                        .setParameter("value", value)
                                                                        .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) states.setMaxResults(cursor.getLimit());

    return states.getResultList();
  }

  @Override
  public @NonNull List<@NonNull ValueState<Value>> findNextWithValue (
    @NonNull final ZonedDateTime date,
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<ValueState<Value>> states = _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ",
        getManagedEntity().getName(),
        " state ",
        " WHERE state.emissionDate > :date ",
        "   AND state.sensorIdentifier IN :sensors ",
        "   AND state.value = :value",
        " ORDER BY state.emissionDate ASC"
      ), getManagedEntity()
    ).setParameter("date", date)
                                                                        .setParameter("sensors", inputSensors)
                                                                        .setParameter("value", value)
                                                                        .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) states.setMaxResults(cursor.getLimit());

    return states.getResultList();
  }

  @Override
  public @NonNull List<@NonNull ValueState<Value>> findAllWithValue (
    @NonNull final List<@NonNull ApplicationEntityReference<? extends Sensor>> inputSensors,
    @NonNull final Value value,
    @NonNull final Cursor cursor
  ) {
    @NonNull final TypedQuery<ValueState<Value>> states = _entityManager.createQuery(
      String.join(
        "",
        "SELECT state ",
        "  FROM ",
        getManagedEntity().getName(),
        " state ",
        " WHERE state.sensorIdentifier IN :sensors ",
        "   AND state.value = :value",
        " ORDER BY state.emissionDate ASC"
      ), getManagedEntity())
                                                                        .setParameter("sensors", inputSensors)
                                                                        .setParameter("value", value)
                                                                        .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) states.setMaxResults(cursor.getLimit());

    return states.getResultList();
  }

}
