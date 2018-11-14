package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

@Component
@Scope("prototype")
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
    @NonNull final EntityManager entityManager, @NonNull final NodeRepository nodes
  ) {
    super(entityManager, Sensor.class);
    _entityManager = entityManager;
    _nodes = nodes;
  }

  @Override
  public @NonNull List<@NonNull Sensor> getSensorsOfType (
    @NonNull final String type
  ) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT sensor FROM ", Sensor.class.getName(), " sensor ",
        " WHERE sensor._type = :type"
      ), Sensor.class
    ).setParameter("type", type)
     .getResultList();
  }

  @Override
  public @NonNull List<@NonNull Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type, @NonNull final Long nodeIdentifier
  ) {
    final Node node = _entityManager.find(
      Node.class,
      nodeIdentifier
    );
    
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT sensor FROM ", Sensor.class.getName(), " sensor ",
        " WHERE sensor._type = :type ",
        "   AND sensor._node IN :nodes"
      ), Sensor.class
    ).setParameter("type", type).setParameter("nodes", _nodes.getAllChildrenOf(node))
                         .getResultList();
  }
}
