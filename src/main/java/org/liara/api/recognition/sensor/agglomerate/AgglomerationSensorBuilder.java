package org.liara.api.recognition.sensor.agglomerate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.io.APIEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AgglomerationSensorBuilder
{
  @Nullable
  private APIEventPublisher _apiEventPublisher;

  @Nullable
  private BooleanValueStateRepository _booleanValues;

  @Nullable
  private CorrelationRepository _correlations;

  public AgglomerationSensorBuilder () {
    _apiEventPublisher = null;
    _booleanValues = null;
    _correlations = null;
  }

  public AgglomerationSensorBuilder (@NonNull final AgglomerationSensorBuilder toCopy) {
    _apiEventPublisher = toCopy.getApiEventPublisher();
    _booleanValues = toCopy.getBooleanValues();
    _correlations = toCopy.getCorrelations();
  }

  public @NonNull AgglomerationSensor build () {
    return new AgglomerationSensor(this);
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

  public @Nullable CorrelationRepository getCorrelations () {
    return _correlations;
  }

  @Autowired
  public void setCorrelations (@Nullable final CorrelationRepository correlations) {
    _correlations = correlations;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof AgglomerationSensorBuilder) {
      @NonNull final AgglomerationSensorBuilder otherAgglomerationSensorBuilder =
        (AgglomerationSensorBuilder) other;

      return (
        Objects.equals(
          _apiEventPublisher,
          otherAgglomerationSensorBuilder.getApiEventPublisher()
        ) &&
        Objects.equals(
          _booleanValues,
          otherAgglomerationSensorBuilder.getBooleanValues()
        ) &&
        Objects.equals(
          _correlations,
          otherAgglomerationSensorBuilder.getCorrelations()
        )
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_apiEventPublisher, _booleanValues, _correlations);
  }
}
