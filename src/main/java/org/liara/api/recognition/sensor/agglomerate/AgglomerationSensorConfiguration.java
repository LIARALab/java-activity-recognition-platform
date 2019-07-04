package org.liara.api.recognition.sensor.agglomerate;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.validation.ApplicationEntityReference;

import java.util.Objects;

public class AgglomerationSensorConfiguration
  implements SensorConfiguration
{
  @NonNull
  private Long _source;

  @NonNegative
  private long _duration;

  public AgglomerationSensorConfiguration () {
    _source = 0L;
    _duration = 0L;
  }

  public AgglomerationSensorConfiguration (@NonNull final AgglomerationSensorConfiguration toCopy) {
    _source = toCopy.getSource();
    _duration = toCopy.getDuration();
  }

  @ApplicationEntityReference(Sensor.class)
  public @NonNull Long getSource () {
    return _source;
  }

  @JsonSetter
  public void setSource (@NonNull final Long source) {
    _source = source;
  }

  public @NonNegative long getDuration () {
    return _duration;
  }

  public void setDuration (@NonNegative final long duration) {
    _duration = duration;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof AgglomerationSensorConfiguration) {
      @NonNull final AgglomerationSensorConfiguration otherAgglomerationSensorConfiguration =
        (AgglomerationSensorConfiguration) other;

      return (
        Objects.equals(
          _duration,
          otherAgglomerationSensorConfiguration.getDuration()
        )
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_duration);
  }
}
