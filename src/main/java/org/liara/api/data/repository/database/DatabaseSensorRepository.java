package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
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
      " WHERE sensor._type = :type",
      Sensor.class
    );

    query.setParameter("type", type);
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);

    return query.getResultList();
  }

  @Override
  public @NonNull List<@NonNull Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type, @NonNull final Long nodeIdentifier
  ) {
    final Node node = _entityManager.find(Node.class, nodeIdentifier);

    @NonNull final TypedQuery<Sensor> query = _entityManager.createQuery(
      "SELECT sensor FROM " + Sensor.class.getName() + " sensor " +
      " WHERE sensor._type = :type " +
      "   AND sensor._node IN :nodes",
      Sensor.class
    );

    query.setParameter("type", type);
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);
    query.setParameter("nodes", _nodes.getAllChildrenOf(node));

    return query.getResultList();
  }
}
