package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DatabaseSensorRepository
       extends DatabaseApplicationEntityRepository<Sensor>
       implements SensorRepository
{
  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final NodeRepository _nodes;

  @Autowired
  public DatabaseSensorRepository(
    @NonNull final EntityManager entityManager,
    @NonNull final NodeRepository nodes
  ) {
    super(entityManager, Sensor.class);
    _entityManager = entityManager;
    _nodes = nodes;
  }

  @Override
  public @NonNull List<@NonNull Sensor> getSensorsOfType (
    @NonNull final String type
  ) {
    @NonNull final TypedQuery<Sensor> query = _entityManager.createQuery(
      "SELECT sensor FROM " + Sensor.class.getName() + " sensor " +
      " WHERE sensor.type = :type",
      Sensor.class
    );

    query.setParameter("type", type);

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type,
    @NonNull final Long nodeIdentifier
  ) {
    final Node node = _entityManager.find(Node.class, nodeIdentifier);

    @NonNull final TypedQuery<Sensor> query = _entityManager.createQuery(
      "SELECT sensor FROM " + Sensor.class.getName() + " sensor" +
      " JOIN " + Node.class.getName() + " node ON node.identifier = sensor.nodeIdentifier" +
      " WHERE sensor.type = :type " +
      "   AND node.coordinates.start >= :start" +
      "   AND node.coordinates.end <= :end",
      Sensor.class
    );

    query.setParameter("type", type);
    query.setParameter("start", node.getCoordinates().getStart());
    query.setParameter("end", node.getCoordinates().getEnd());

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Sensor> getSensorsWithName (final @NonNull String name) {
    @NonNull final TypedQuery<Sensor> query = _entityManager.createQuery(
      "SELECT sensor FROM " + Sensor.class.getName() + " sensor " +
      " WHERE sensor.name = :name",
      Sensor.class
    );

    query.setParameter("name", name);

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Sensor> getSensorsWithNameIntoNode (
    final @NonNull String name, final @NonNull Long nodeIdentifier
  ) {
    final Node node = _entityManager.find(Node.class, nodeIdentifier);

    @NonNull final TypedQuery<Sensor> query = _entityManager.createQuery(
      "SELECT sensor FROM " + Sensor.class.getName() + " sensor " +
      " JOIN " + Node.class.getName() + " node ON node.identifier = sensor.nodeIdentifier" +
      " WHERE sensor.name = :name " +
      "   AND node.coordinates.start >= :start" +
      "   AND node.coordinates.end <= :end",
      Sensor.class
    );

    query.setParameter("name", name);
    query.setParameter("start", node.getCoordinates().getStart());
    query.setParameter("end", node.getCoordinates().getEnd());

    return query.getResultList();
  }
}
