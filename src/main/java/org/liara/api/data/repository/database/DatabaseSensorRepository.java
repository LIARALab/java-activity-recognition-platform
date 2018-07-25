package org.liara.api.data.repository.database;

import java.util.List;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.repository.SensorRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Primary
public class DatabaseSensorRepository
       extends DatabaseApplicationEntityRepository<Sensor>
       implements SensorRepository
{
  @NonNull
  private final EntityManager _entityManager;
  
  public DatabaseSensorRepository(
    @NonNull final EntityManager entityManager
  ) {
    super(entityManager, Sensor.class);
    _entityManager = entityManager;
  }

  @Override
  public List<Sensor> getSensorsOfType (
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
  public List<Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type, 
    @NonNull final ApplicationEntityReference<Node> nodeReference
  ) {
    final Node node = _entityManager.find(Node.class, nodeReference.getIdentifier());
    
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT sensor FROM ", Sensor.class.getName(), " sensor ",
        " WHERE sensor._type = :type ",
        "   AND sensor._node IN :nodes"
      ), Sensor.class
    ).setParameter("type", type)
     .setParameter("nodes", node.getAllChildren())
     .getResultList();
  }
}
