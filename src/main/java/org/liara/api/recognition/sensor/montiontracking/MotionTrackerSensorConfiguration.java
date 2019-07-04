package org.liara.api.recognition.sensor.montiontracking;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.validation.ApplicationEntityReference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MotionTrackerSensorConfiguration
  implements SensorConfiguration
{
  @NonNull
  private Long _source;

  @NonNull
  private final Set<@NonNull Long> _validInputs;
  
  @NonNull
  private final Set<@NonNull Long> _ignoredInputs;

  @NonNull
  private final Set<@NonNull Long> _validNodes;

  @NonNull
  private final Set<@NonNull Long> _ignoredNodes;

  public MotionTrackerSensorConfiguration () {
    _validInputs = new HashSet<>();
    _ignoredInputs = new HashSet<>();
    _validNodes = new HashSet<>();
    _ignoredNodes = new HashSet<>();
    _source = 0L;
  }

  public MotionTrackerSensorConfiguration (
    @NonNull final MotionTrackerSensorConfiguration toCopy
  ) {
    _validInputs = new HashSet<>(toCopy.getValidInputs());
    _ignoredInputs = new HashSet<>(toCopy.getIgnoredInputs());
    _validNodes = new HashSet<>(toCopy.getValidNodes());
    _ignoredNodes = new HashSet<>(toCopy.getIgnoredNodes());
    _source = toCopy.getSource();
  }

  @ApplicationEntityReference(Sensor.class)
  public @NonNull Long getSource () {
    return _source;
  }

  @JsonSetter
  public void setSource (@NonNull final Long source) {
    _source = source;
  }

  @ApplicationEntityReference(Sensor.class)
  public @NonNull Set<@NonNull Long> getValidInputs () {
    return _validInputs;
  }
  
  @JsonSetter
  public void setValidInputs (@Nullable final Collection<@NonNull Long> inputs) {
    _validInputs.clear();
    if (inputs != null) _validInputs.addAll(inputs);
  }

  @ApplicationEntityReference(Sensor.class)
  public @NonNull Set<@NonNull Long> getIgnoredInputs () {
    return _ignoredInputs;
  }
  
  @JsonSetter
  public void setIgnoredInputs (@Nullable final Collection<@NonNull Long> ignored) {
    _ignoredInputs.clear();
    if (ignored != null) _ignoredInputs.addAll(ignored);
  }

  @ApplicationEntityReference(Node.class)
  public @NonNull Set<@NonNull Long> getIgnoredNodes () {
    return _ignoredNodes;
  }

  @JsonSetter
  public void setIgnoredNodes (@Nullable final Collection<@NonNull Long> ignored) {
    _ignoredNodes.clear();
    if (ignored != null) _ignoredNodes.addAll(ignored);
  }

  @ApplicationEntityReference(Node.class)
  public @NonNull Set<@NonNull Long> getValidNodes () {
    return _validNodes;
  }

  @JsonSetter
  public void setValidNodes (@Nullable final Collection<@NonNull Long> ignored) {
    _validNodes.clear();
    if (ignored != null) _validNodes.addAll(ignored);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof MotionTrackerSensorConfiguration) {
      @NonNull
      final MotionTrackerSensorConfiguration otherMotionTrackerSensorConfiguration =
        (MotionTrackerSensorConfiguration) other;

      return Objects.equals(
        _validInputs,
        otherMotionTrackerSensorConfiguration.getValidInputs()
      ) && Objects.equals(
        _ignoredInputs,
        otherMotionTrackerSensorConfiguration.getIgnoredInputs()
      ) && Objects.equals(
        _validNodes,
        otherMotionTrackerSensorConfiguration.getValidNodes()
      ) && Objects.equals(
        _ignoredNodes,
        otherMotionTrackerSensorConfiguration.getIgnoredNodes()
      ) && Objects.equals(
        _source,
        otherMotionTrackerSensorConfiguration.getSource()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_validInputs, _ignoredInputs, _validNodes, _ignoredNodes, _source);
  }
}
