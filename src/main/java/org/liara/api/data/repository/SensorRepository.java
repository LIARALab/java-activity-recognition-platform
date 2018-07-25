package org.liara.api.data.repository;

import java.util.List;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.lang.NonNull;

public interface SensorRepository extends ApplicationEntityRepository<Sensor>
{
  public default List<Sensor> getSensorsOfType (@NonNull final Class<?> type) {
    return getSensorsOfType(type.getName());
  }
  
  public List<Sensor> getSensorsOfType (@NonNull final String type);
  
  public default List<Sensor> getSensorsOfTypeIntoNode (
    @NonNull final Class<?> type,
    @NonNull final ApplicationEntityReference<Node> node
  ) {
    return getSensorsOfTypeIntoNode(type.getName(), node);
  }
  
  public List<Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type, 
    @NonNull final ApplicationEntityReference<Node> node
  );
}
