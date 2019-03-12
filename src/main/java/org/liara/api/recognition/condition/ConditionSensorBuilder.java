package org.liara.api.recognition.condition;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.repository.AnyStateRepository;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ConditionSensorBuilder
{
  @Nullable
  private ApplicationEventPublisher _applicationEventPublisher;

  @Nullable
  private CorrelationRepository _correlationRepository;

  private AnyStateRepository _inputStateRepository;

  private BooleanValueStateRepository _outputStateRepository;

  public ConditionSensorBuilder () {
    _applicationEventPublisher = null;
    _correlationRepository = null;
    _inputStateRepository = null;
    _outputStateRepository = null;
  }

  public @Nullable ApplicationEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  @Autowired
  public void setApplicationEventPublisher (
    @Nullable final ApplicationEventPublisher applicationEventPublisher
  ) {
    _applicationEventPublisher = applicationEventPublisher;
  }

  public @Nullable CorrelationRepository getCorrelationRepository () {
    return _correlationRepository;
  }

  @Autowired
  public void setCorrelationRepository (
    @Nullable final CorrelationRepository correlationRepository
  ) {
    _correlationRepository = correlationRepository;
  }

  public AnyStateRepository getInputStateRepository () {
    return _inputStateRepository;
  }

  @Autowired
  public void setInputStateRepository (final AnyStateRepository inputStateRepository) {
    _inputStateRepository = inputStateRepository;
  }

  public BooleanValueStateRepository getOutputStateRepository () {
    return _outputStateRepository;
  }

  @Autowired
  public void setOutputStateRepository (
    final BooleanValueStateRepository outputStateRepository
  ) {
    _outputStateRepository = outputStateRepository;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ConditionSensorBuilder) {
      @NonNull
      final ConditionSensorBuilder otherConditionSensorBuilder = (ConditionSensorBuilder) other;

      return Objects.equals(
        _applicationEventPublisher,
        otherConditionSensorBuilder.getApplicationEventPublisher()
      ) && Objects.equals(
        _correlationRepository,
        otherConditionSensorBuilder.getCorrelationRepository()
      ) && Objects.equals(
        _inputStateRepository,
        otherConditionSensorBuilder.getInputStateRepository()
      ) && Objects.equals(
        _outputStateRepository,
        otherConditionSensorBuilder.getOutputStateRepository()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _applicationEventPublisher,
      _correlationRepository,
      _inputStateRepository,
      _outputStateRepository
    );
  }
}
