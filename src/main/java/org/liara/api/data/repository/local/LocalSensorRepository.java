package org.liara.api.data.repository.local;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.repository.SensorRepository;
import org.springframework.lang.NonNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class LocalSensorRepository
       extends LocalApplicationEntityRepository<Sensor>
       implements SensorRepository
{
  @NonNull
  private LocalNodeRepository _nodes;

  @Nullable
  private Map<String, Set<Sensor>> _sensorsByType;

  public static LocalSensorRepository from (@NonNull final ApplicationEntityManager manager) {
    final LocalSensorRepository result = new LocalSensorRepository();
    manager.addListener(result);
    return result;
  }
  
  public LocalSensorRepository() {
    super(Sensor.class);
    _sensorsByType = new HashMap<>();
    _nodes = new LocalNodeRepository();
  }
  
  @Override
  public List<Sensor> getSensorsOfType (
    @NonNull final String type
  ) {
    if (_sensorsByType.containsKey(type)) {
      return new ArrayList<>(_sensorsByType.get(type));
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public List<Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type, @NonNull final ApplicationEntityReference<? extends Node> node
  ) {
    return getSensorsOfType(type).stream().filter(sensor -> {
      Node parent = sensor.getNode();
      
      while (parent != null) {
        if (Objects.equals(parent.getReference(), node)) {
          return true;
        } else {
          parent = _nodes.getParentOf(parent);
        }
      }
      
      return false;
    }).collect(Collectors.toList());
  }

  @Override
  protected void entityWasAdded (@NonNull final ApplicationEntity entity) {
    super.entityWasAdded(entity);
    _nodes.entityWasAdded(entity);
  }

  @Override
  protected void entityWasRemoved (@NonNull final ApplicationEntity entity) {
    super.entityWasRemoved(entity);
    _nodes.entityWasRemoved(entity);
  }

  @Override
  protected void trackedEntityWasAdded (@NonNull final Sensor entity) {
    super.trackedEntityWasAdded(entity);
    
    if (!_sensorsByType.containsKey(entity.getType())) {
      _sensorsByType.put(entity.getType(), new HashSet<>());
    }
    
    _sensorsByType.get(entity.getType()).add(entity);
  }

  @Override
  protected void trackedEntityWasRemoved (@NonNull final Sensor entity) {
    super.trackedEntityWasRemoved(entity);
    
    _sensorsByType.get(entity.getType()).remove(entity);
    
    if (_sensorsByType.get(entity.getType()).isEmpty()) {
      _sensorsByType.remove(entity.getType());
    }
  }
}
