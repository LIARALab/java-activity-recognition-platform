package org.liara.api.recognition.sensor.motionmapping;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.validation.ApplicationEntityReference;

import java.util.Objects;

public class MotionMapperSensorConfiguration
  implements SensorConfiguration
{
  @NonNull
  private Long _nodeIdentifier;

  public MotionMapperSensorConfiguration () {
    _nodeIdentifier = 0L;
  }

  public MotionMapperSensorConfiguration (@NonNull final MotionMapperSensorConfiguration toCopy) {
    _nodeIdentifier = toCopy.getNodeIdentifier();
  }

  @ApplicationEntityReference(Node.class)
  public @NonNull Long getNodeIdentifier () {
    return _nodeIdentifier;
  }

  @JsonSetter
  public void setNodeIdentifier (@NonNull final Long identifier) {
    _nodeIdentifier = identifier;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof MotionMapperSensorConfiguration) {
      @NonNull
      final MotionMapperSensorConfiguration otherMotionMapperSensorConfiguration =
        (MotionMapperSensorConfiguration) other;

      return Objects.equals(
        _nodeIdentifier,
        otherMotionMapperSensorConfiguration.getNodeIdentifier()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_nodeIdentifier);
  }
}
