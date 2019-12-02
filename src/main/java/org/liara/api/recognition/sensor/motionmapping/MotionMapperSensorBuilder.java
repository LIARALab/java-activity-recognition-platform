package org.liara.api.recognition.sensor.motionmapping;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.repository.*;
import org.liara.api.io.APIEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MotionMapperSensorBuilder
{
  @Nullable
  private APIEventPublisher _apiEventPublisher;

  @Nullable
  private AnyStateRepository _booleanValues;

  @Nullable
  private LongValueStateRepository _longValues;

  @Nullable
  private SensorRepository _sensors;

  @Nullable
  private CorrelationRepository _correlations;

  @Nullable
  private NodeRepository _nodes;

  public MotionMapperSensorBuilder () {
    _apiEventPublisher = null;
    _sensors = null;
    _correlations = null;
    _nodes = null;
    _booleanValues = null;
    _longValues = null;
  }

  public MotionMapperSensorBuilder (
    @NonNull final MotionMapperSensorBuilder toCopy
  ) {
    _apiEventPublisher = toCopy.getApiEventPublisher();
    _sensors = toCopy.getSensors();
    _correlations = toCopy.getCorrelations();
    _nodes = toCopy.getNodes();
    _booleanValues = toCopy.getBooleanValues();
    _longValues = toCopy.getLongValues();
  }

  public @NonNull MotionMapperSensor build () {
    return new MotionMapperSensor(this);
  }

  public @Nullable AnyStateRepository getBooleanValues () {
    return _booleanValues;
  }

  @Autowired
  public void setBooleanValues (@Nullable final AnyStateRepository booleanValues) {
    _booleanValues = booleanValues;
  }

  public @Nullable LongValueStateRepository getLongValues () {
    return _longValues;
  }

  @Autowired
  public void setLongValues (@Nullable final LongValueStateRepository longValues) {
    _longValues = longValues;
  }

  public @Nullable APIEventPublisher getApiEventPublisher () {
    return _apiEventPublisher;
  }

  @Autowired
  public void setApiEventPublisher (@Nullable final APIEventPublisher apiEventPublisher) {
    _apiEventPublisher = apiEventPublisher;
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

    if (other instanceof MotionMapperSensorBuilder) {
      @NonNull final MotionMapperSensorBuilder otherMotionMapperSensorBuilder =
        (MotionMapperSensorBuilder) other;

      return Objects.equals(
        _apiEventPublisher,
        otherMotionMapperSensorBuilder.getApiEventPublisher()
      ) && Objects.equals(
        _sensors,
        otherMotionMapperSensorBuilder.getSensors()
      ) && Objects.equals(
        _correlations,
        otherMotionMapperSensorBuilder.getCorrelations()
      ) && Objects.equals(
        _nodes,
        otherMotionMapperSensorBuilder.getNodes()
      ) && Objects.equals(
        _booleanValues,
        otherMotionMapperSensorBuilder.getBooleanValues()
      ) && Objects.equals(
        _longValues,
        otherMotionMapperSensorBuilder.getLongValues()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _apiEventPublisher, _sensors, _correlations, _nodes,
      _booleanValues, _longValues
    );
  }
}
