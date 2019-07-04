package org.liara.api.recognition.sensor.montiontracking;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.LongValueState;
import org.liara.api.recognition.sensor.type.ValueSensorType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MotionTrackerSensorAsserter
{
  @NonNull
  private final MotionTrackerSensor _sensor;

  @NonNull
  private final Set<@NonNull Long> _ignoredSensors;

  @NonNull
  private final Set<@NonNull Long> _validSensors;

  public MotionTrackerSensorAsserter (@NonNull final MotionTrackerSensor sensor) {
    _sensor = sensor;
    _ignoredSensors = new HashSet<>();
    _validSensors = new HashSet<>();
  }

  public void refresh () {
    computeIgnoredSensors();
    computeValidSensors();
  }

  private void computeIgnoredSensors () {
    _ignoredSensors.clear();

    for (@NonNull final Long ignoredNodeIdentifier : _sensor.getConfiguration().getIgnoredNodes()) {
      @NonNull final List<@NonNull Sensor> ignored = _sensor.getSensors().getSensorsOfTypeIntoNode(
        ValueSensorType.MOTION.getName(), ignoredNodeIdentifier
      );

      ignored.stream().map(Sensor::getIdentifier)
             .map(Objects::requireNonNull)
             .forEach(_ignoredSensors::add);
    }

    _sensor.getConfiguration()
           .getIgnoredInputs()
           .stream()
           .map(Objects::requireNonNull)
           .forEach(_ignoredSensors::add);
  }

  private void computeValidSensors () {
    _validSensors.clear();

    for (@NonNull final Long validNodeIdentifier : _sensor.getConfiguration().getValidNodes()) {
      @NonNull final List<@NonNull Sensor> valids = _sensor.getSensors().getSensorsOfTypeIntoNode(
        ValueSensorType.MOTION.getName(), validNodeIdentifier
      );

      valids.stream().map(Sensor::getIdentifier)
            .map(Objects::requireNonNull)
            .forEach(_validSensors::add);
    }

    _sensor.getConfiguration()
           .getValidInputs()
           .stream()
           .map(Objects::requireNonNull)
           .forEach(_validSensors::add);
  }

  public boolean isIgnoredInput (@NonNull final LongValueState state) {
    return _ignoredSensors.contains(state.getValue());
  }

  public boolean isValidInput (@NonNull final LongValueState state) {
    return _validSensors.contains(state.getValue());
  }

  public @NonNull Set<@NonNull Long> getIgnoredSensors () {
    return _ignoredSensors;
  }

  public @NonNull Set<@NonNull Long> getValidSensors () {
    return _validSensors;
  }
}
