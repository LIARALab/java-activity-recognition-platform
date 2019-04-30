package org.liara.api.recognition.sensor.onevsall;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.recognition.sensor.type.ValueSensorType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OneVsAllToUpDownMotionSensorAsserter
{
  @NonNull
  private final OneVsAllToUpDownMotionSensor _sensor;

  @NonNull
  private final Set<@NonNull Long> _trackedSensors;

  @NonNull
  private final Set<@NonNull Long> _ignoredSensors;

  @NonNull
  private final Set<@NonNull Long> _validSensors;

  public OneVsAllToUpDownMotionSensorAsserter (@NonNull final OneVsAllToUpDownMotionSensor sensor) {
    _sensor = sensor;
    _trackedSensors = new HashSet<>();
    _ignoredSensors = new HashSet<>();
    _validSensors = new HashSet<>();
  }

  public void refresh () {
    computeIgnoredSensors();
    computeValidSensors();
    computeTrackedSensors();
  }

  private void computeTrackedSensors () {
    _trackedSensors.clear();

    @NonNull final Sensor sensor = _sensor.getSensor().orElseThrow();
    Objects.requireNonNull(sensor.getNodeIdentifier());

    @NonNull final Node sensorNode = _sensor.getNodes().getAt(sensor.getNodeIdentifier());
    @NonNull final Node rootNode   = _sensor.getNodes().getRoot(sensorNode);

    @NonNull final List<@NonNull Sensor> sensors = _sensor.getSensors().getSensorsOfTypeIntoNode(
      ValueSensorType.MOTION.getName(),
      Objects.requireNonNull(rootNode.getIdentifier())
    );

    sensors.stream().map(Sensor::getIdentifier)
           .map(Objects::requireNonNull)
           .filter(x -> !isIgnoredInput(x))
           .forEach(_trackedSensors::add);
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

  public boolean isIgnoredInput (@NonNull final Long sensorIdentifier) {
    return _ignoredSensors.contains(sensorIdentifier);
  }

  public boolean isIgnoredInput (@NonNull final State state) {
    return _ignoredSensors.contains(state.getSensorIdentifier());
  }

  public boolean isValidInput (@NonNull final Long sensorIdentifier) {
    return _validSensors.contains(sensorIdentifier);
  }

  public boolean isValidInput (@NonNull final State state) {
    return _validSensors.contains(state.getSensorIdentifier());
  }

  public boolean isTracked (@NonNull final Long sensorIdentifier) {
    return _trackedSensors.contains(sensorIdentifier);
  }

  public boolean isTracked (@NonNull final State state) {
    return _trackedSensors.contains(state.getSensorIdentifier());
  }

  public @NonNull Set<@NonNull Long> getTrackedSensors () {
    return _trackedSensors;
  }

  public @NonNull Set<@NonNull Long> getIgnoredSensors () {
    return _ignoredSensors;
  }

  public @NonNull Set<@NonNull Long> getValidSensors () {
    return _validSensors;
  }
}
