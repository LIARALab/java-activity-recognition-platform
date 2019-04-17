package org.liara.api.recognition.sensor.onevsall;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SensorRepository;
import org.liara.api.io.APIEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class OneVsAllToUpDownMotionSensorBuilder
{
  @Nullable
  private APIEventPublisher _apiEventPublisher;

  @Nullable
  private BooleanValueStateRepository _flags;

  @Nullable
  private SensorRepository _sensors;

  @Nullable
  private CorrelationRepository _correlations;

  @Nullable
  private NodeRepository _nodes;

  public OneVsAllToUpDownMotionSensorBuilder () {
    _apiEventPublisher = null;
    _flags = null;
    _sensors = null;
    _correlations = null;
    _nodes = null;
  }

  public OneVsAllToUpDownMotionSensorBuilder (
    @NonNull final OneVsAllToUpDownMotionSensorBuilder toCopy
  ) {
    _apiEventPublisher = toCopy.getApiEventPublisher();
    _flags = toCopy.getFlags();
    _sensors = toCopy.getSensors();
    _correlations = toCopy.getCorrelations();
    _nodes = toCopy.getNodes();
  }

  public @NonNull OneVsAllToUpDownMotionSensor build () {
    return new OneVsAllToUpDownMotionSensor(this);
  }

  public @Nullable APIEventPublisher getApiEventPublisher () {
    return _apiEventPublisher;
  }

  @Autowired
  public void setApiEventPublisher (@Nullable final APIEventPublisher apiEventPublisher) {
    _apiEventPublisher = apiEventPublisher;
  }

  public @Nullable BooleanValueStateRepository getFlags () {
    return _flags;
  }

  @Autowired
  public void setFlags (@Nullable final BooleanValueStateRepository flags) {
    _flags = flags;
  }

  public @Nullable SensorRepository getSensors () {
    return _sensors;
  }

  @Autowired
  public void setSensors (@Nullable final SensorRepository sensors) {
    _sensors = sensors;
  }

  public @Nullable CorrelationRepository getCorrelations () {
    return _correlations;
  }

  @Autowired
  public void setCorrelations (@Nullable final CorrelationRepository correlations) {
    _correlations = correlations;
  }

  public @Nullable NodeRepository getNodes () {
    return _nodes;
  }

  @Autowired
  public void setNodes (@Nullable final NodeRepository nodes) {
    _nodes = nodes;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof OneVsAllToUpDownMotionSensorBuilder) {
      @NonNull final OneVsAllToUpDownMotionSensorBuilder otherOneVsAllToUpDownMotionSensorBuilder =
        (OneVsAllToUpDownMotionSensorBuilder) other;

      return Objects.equals(
        _apiEventPublisher,
        otherOneVsAllToUpDownMotionSensorBuilder.getApiEventPublisher()
      ) && Objects.equals(
        _flags,
        otherOneVsAllToUpDownMotionSensorBuilder.getFlags()
      ) && Objects.equals(
        _sensors,
        otherOneVsAllToUpDownMotionSensorBuilder.getSensors()
      ) && Objects.equals(
        _correlations,
        otherOneVsAllToUpDownMotionSensorBuilder.getCorrelations()
      ) && Objects.equals(
        _nodes,
        otherOneVsAllToUpDownMotionSensorBuilder.getNodes()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_apiEventPublisher, _flags, _sensors, _correlations, _nodes);
  }
}
