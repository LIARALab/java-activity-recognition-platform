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
  private APIEventPublisher _eventPublisher;

  @Nullable
  private BooleanValueStateRepository _booleanStateRepository;

  @Nullable
  private CorrelationRepository _correlationRepository;

  public AgglomerationSensorBuilder () {
    _eventPublisher = null;
    _booleanStateRepository = null;
    _correlationRepository = null;
  }

  public AgglomerationSensorBuilder (@NonNull final AgglomerationSensorBuilder toCopy) {
    _eventPublisher = toCopy.getEventPublisher();
    _booleanStateRepository = toCopy.getBooleanStateRepository();
    _correlationRepository = toCopy.getCorrelationRepository();
  }

  public @NonNull AgglomerationSensor build () {
    return new AgglomerationSensor(this);
  }

  public @Nullable APIEventPublisher getEventPublisher() {
    return _eventPublisher;
  }

  @Autowired
  public void setEventPublisher (@Nullable final APIEventPublisher eventPublisher) {
    _eventPublisher = eventPublisher;
  }

  public @Nullable BooleanValueStateRepository getBooleanStateRepository() {
    return _booleanStateRepository;
  }

  @Autowired
  public void setBooleanStateRepository(@Nullable final BooleanValueStateRepository booleanStateRepository) {
    _booleanStateRepository = booleanStateRepository;
  }

  public @Nullable CorrelationRepository getCorrelationRepository() {
    return _correlationRepository;
  }

  @Autowired
  public void setCorrelationRepository (@Nullable final CorrelationRepository correlationRepository) {
    _correlationRepository = correlationRepository;
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
                _eventPublisher,
          otherAgglomerationSensorBuilder.getEventPublisher()
        ) &&
        Objects.equals(
                _booleanStateRepository,
          otherAgglomerationSensorBuilder.getBooleanStateRepository()
        ) &&
        Objects.equals(
                _correlationRepository,
          otherAgglomerationSensorBuilder.getCorrelationRepository()
        )
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_eventPublisher, _booleanStateRepository, _correlationRepository);
  }
}
