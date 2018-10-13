package org.liara.api.data.repository;

import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.springframework.lang.NonNull;

import java.util.List;

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
