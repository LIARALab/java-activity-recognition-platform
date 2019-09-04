package org.liara.api.recognition.sensor.motionmapping;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.recognition.sensor.type.ValueSensorType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MotionMapperSensorAsserter
{
  @NonNull
  private final MotionMapperSensor _sensor;

  @NonNull
  private final Set<@NonNull Long> _trackedSensors;

  public MotionMapperSensorAsserter (@NonNull final MotionMapperSensor sensor) {
    _sensor = sensor;
    _trackedSensors = new HashSet<>();
  }

  public void refresh () {
    computeTrackedSensors();
  }

  private void computeTrackedSensors () {
    _trackedSensors.clear();

    @NonNull final Node rootNode = _sensor.getNodes().getAt(
      _sensor.getConfiguration().getNodeIdentifier()
    );

    _sensor.getSensors().getSensorsOfTypeIntoNode(
      ValueSensorType.MOTION.getName(),
      Objects.requireNonNull(rootNode.getIdentifier())
    ).stream().map(Objects::requireNonNull)
           .map(Sensor::getIdentifier)
           .forEach(_trackedSensors::add);

    _sensor.getSensors().getSensorsWithNameIntoNode(
      "contact",
      Objects.requireNonNull(rootNode.getIdentifier())
    ).stream().map(Objects::requireNonNull)
           .map(Sensor::getIdentifier)
           .forEach(_trackedSensors::add);
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
}
