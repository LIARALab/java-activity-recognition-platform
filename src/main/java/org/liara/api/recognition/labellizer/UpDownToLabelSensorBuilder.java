package org.liara.api.recognition.labellizer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.liara.api.data.repository.CorrelationRepository;
import org.liara.api.data.repository.LabelStateRepository;
import org.liara.api.io.APIEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UpDownToLabelSensorBuilder
{
  @Nullable
  private APIEventPublisher _publisher;

  @Nullable
  private LabelStateRepository _outputs;

  private BooleanValueStateRepository _inputs;

  @Nullable
  private CorrelationRepository _correlations;

  public UpDownToLabelSensorBuilder () {
    _publisher = null;
    _outputs = null;
    _inputs = null;
    _correlations = null;
  }

  public @NonNull UpDownToLabelSensor build () {
    return new UpDownToLabelSensor(this);
  }

  public @Nullable APIEventPublisher getPublisher () {
    return _publisher;
  }

  @Autowired
  public void setPublisher (@Nullable final APIEventPublisher publisher) {
    _publisher = publisher;
  }

  public @Nullable LabelStateRepository getOutputs () {
    return _outputs;
  }

  @Autowired
  public void setOutputs (@Nullable final LabelStateRepository outputs) {
    _outputs = outputs;
  }

  public BooleanValueStateRepository getInputs () {
    return _inputs;
  }

  @Autowired
  public void setInputs (final BooleanValueStateRepository inputs) {
    _inputs = inputs;
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

    if (other instanceof UpDownToLabelSensorBuilder) {
      @NonNull final UpDownToLabelSensorBuilder otherUpDownToLabelSensorBuilder =
        (UpDownToLabelSensorBuilder) other;

      return Objects.equals(
        _publisher,
        otherUpDownToLabelSensorBuilder.getPublisher()
      ) && Objects.equals(
        _outputs,
        otherUpDownToLabelSensorBuilder.getOutputs()
      ) && Objects.equals(
        _inputs,
        otherUpDownToLabelSensorBuilder.getInputs()
      ) && Objects.equals(
        _correlations,
        otherUpDownToLabelSensorBuilder.getCorrelations()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_publisher, _outputs, _inputs, _correlations);
  }
}
