package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SensorRepository;
import org.liara.api.utils.Duplicator;

import java.util.*;
import java.util.stream.Collectors;

public class LocalSensorRepository
       extends LocalApplicationEntityRepository<Sensor>
       implements SensorRepository
{
  @Nullable
  private Map<@NonNull String, @NonNull Set<@NonNull Sensor>> _sensorsByType;
  
  public LocalSensorRepository() {
    super(Sensor.class);
    _sensorsByType = new HashMap<>();
  }
  
  @Override
  public @NonNull List<@NonNull Sensor> getSensorsOfType (
    @NonNull final String type
  ) {
    if (_sensorsByType.containsKey(type)) {
      return _sensorsByType.get(type)
                           .stream()
                           .map(Duplicator::duplicate).sorted(Comparator.comparing(ApplicationEntity::getIdentifier))
                           .collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public @NonNull List<@NonNull Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type, @NonNull final Long nodeIdentifier
  ) {
    if (getParent() == null) return Collections.emptyList();

    @NonNull final NodeRepository nodeRepository = getParent().repository(LocalNodeRepository.class);

    return getSensorsOfType(type).stream().filter(sensor -> {
      @Nullable Node parent = nodeRepository.find(sensor.getNodeIdentifier()).get();
      
      while (parent != null) {
        if (Objects.equals(
          parent.getIdentifier(),
          nodeIdentifier
        )) {
          return true;
        } else {
          parent = nodeRepository.getParentOf(parent);
        }
      }
      
      return false;
    }).sorted(Comparator.comparing(ApplicationEntity::getIdentifier)).collect(Collectors.toList());
  }

  @Override
  public void onUpdate (
    @Nullable final ApplicationEntity oldEntity, @NonNull final ApplicationEntity newEntity
  )
  {
    super.onUpdate(oldEntity, newEntity);

    if (newEntity instanceof Sensor) {
      @Nullable final Sensor oldSensor = (Sensor) oldEntity;
      @NonNull final Sensor  newSensor = (Sensor) newEntity;

      if (oldSensor != null) {
        _sensorsByType.get(oldSensor.getType()).remove(oldSensor);
      } else if (!_sensorsByType.containsKey(newSensor.getType())) {
        _sensorsByType.put(
          newSensor.getType().getName(),
          new HashSet<>()
        );
      }

      _sensorsByType.get(newSensor.getType()).add(newSensor);
    }
  }

  @Override
  public void onRemove (@NonNull final ApplicationEntity entity) {
    super.onRemove(entity);

    if (entity instanceof Sensor) {
      @NonNull final Sensor sensor = (Sensor) entity;

      _sensorsByType.get(sensor.getType()).remove(sensor);

      if (_sensorsByType.get(sensor.getType()).isEmpty()) {
        _sensorsByType.remove(sensor.getType());
      }
    }
  }
}
