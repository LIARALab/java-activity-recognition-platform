package org.liara.api.recognition.sensor.montiontracking;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.repository.SensorRepository;
import org.liara.api.data.series.SeriesManager;
import org.liara.api.io.APIEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MotionTrackerSensorBuilder
{
  @Nullable
  private APIEventPublisher _apiEventPublisher;

  @Nullable
  private BooleanValueStateRepository _booleanValues;

  @Nullable
  private SeriesManager _seriesManager;

  @Nullable
  private SensorRepository _sensors;

  @Nullable
  private CorrelationRepository _correlations;

  @Nullable
  private NodeRepository _nodes;

  public MotionTrackerSensorBuilder () {
    _apiEventPublisher = null;
    _booleanValues = null;
    _sensors = null;
    _correlations = null;
    _nodes = null;
    _seriesManager = null;
  }

  public MotionTrackerSensorBuilder (
    @NonNull final MotionTrackerSensorBuilder toCopy
  ) {
    _apiEventPublisher = toCopy.getApiEventPublisher();
    _booleanValues = toCopy.getBooleanValues();
    _sensors = toCopy.getSensors();
    _correlations = toCopy.getCorrelations();
    _nodes = toCopy.getNodes();
    _seriesManager = toCopy.getSeriesManager();
  }

  public @NonNull MotionTrackerSensor build () {
    return new MotionTrackerSensor(this);
  }

  public @Nullable SeriesManager getSeriesManager () {
    return _seriesManager;
  }

  @Autowired
  public void setSeriesManager (@Nullable final SeriesManager seriesManager) {
    _seriesManager = seriesManager;
  }

  public @Nullable APIEventPublisher getApiEventPublisher () {
    return _apiEventPublisher;
  }

  @Autowired
  public void setApiEventPublisher (@Nullable final APIEventPublisher apiEventPublisher) {
    _apiEventPublisher = apiEventPublisher;
  }

  public @Nullable BooleanValueStateRepository getBooleanValues () {
    return _booleanValues;
  }

  @Autowired
  public void setBooleanValues (@Nullable final BooleanValueStateRepository booleanValues) {
    _booleanValues = booleanValues;
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

    if (other instanceof MotionTrackerSensorBuilder) {
      @NonNull final MotionTrackerSensorBuilder otherMotionTrackerSensorBuilder =
        (MotionTrackerSensorBuilder) other;

      return Objects.equals(
        _apiEventPublisher,
        otherMotionTrackerSensorBuilder.getApiEventPublisher()
      ) && Objects.equals(
        _booleanValues,
        otherMotionTrackerSensorBuilder.getBooleanValues()
      ) && Objects.equals(
        _sensors,
        otherMotionTrackerSensorBuilder.getSensors()
      ) && Objects.equals(
        _correlations,
        otherMotionTrackerSensorBuilder.getCorrelations()
      ) && Objects.equals(
        _nodes,
        otherMotionTrackerSensorBuilder.getNodes()
      ) && Objects.equals(
        _seriesManager,
        otherMotionTrackerSensorBuilder.getSeriesManager()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _apiEventPublisher, _booleanValues, _sensors,
      _correlations, _nodes, _seriesManager
    );
  }
}
