package org.liara.api.recognition.sensor.type;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.SensorType;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SensorTypeManager
{
  @NonNull
  private Map<@NonNull String, @NonNull SensorType> _sensorTypes;

  public SensorTypeManager () {
    _sensorTypes = new HashMap<>();
  }

  public SensorTypeManager (@NonNull final SensorTypeManager toCopy) {
    _sensorTypes = new HashMap<>(toCopy._sensorTypes);
  }

  public void register (@NonNull final SensorType sensorType) {
    if (_sensorTypes.containsKey(sensorType.getName())) {
      if (!Objects.equals(get(sensorType.getName()), sensorType)) {
        throw new Error(
          "Unable to register the sensor type " + sensorType.toString() + " aliased as " + sensorType.getName() +
          " because another registered type has the same name " + get(sensorType.getName()).toString());
      }
    } else {
      _sensorTypes.put(sensorType.getName(), sensorType);
    }
  }

  public @NonNull SensorType get (@NonNull final String name) {
    return _sensorTypes.get(name);
  }

  public boolean contains (@NonNull final String name) {
    return _sensorTypes.containsKey(name);
  }

  public boolean contains (@NonNull final SensorType sensorType) {
    return _sensorTypes.containsKey(sensorType.getName()) && _sensorTypes.get(sensorType.getName()).equals(sensorType);
  }

  public @NonNull Set<@NonNull SensorType> getRegisteredTypes () {
    return new HashSet<>(_sensorTypes.values());
  }

  public int getSize () {
    return _sensorTypes.size();
  }

  @Override
  public int hashCode () {
    return Objects.hash(_sensorTypes);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof SensorTypeManager) {
      @NonNull final SensorTypeManager otherManager = (SensorTypeManager) other;

      return Objects.equals(_sensorTypes, otherManager._sensorTypes);
    }

    return false;
  }
}
