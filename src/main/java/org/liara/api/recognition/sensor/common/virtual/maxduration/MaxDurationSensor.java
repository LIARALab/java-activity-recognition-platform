package org.liara.api.recognition.sensor.common.virtual.maxduration;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.data.entity.state.LabelStateCreationSchema;
import org.liara.api.data.entity.state.LabelStateSnapshot;
import org.liara.api.data.entity.state.StateDeletionSchema;
import org.liara.api.data.repository.LabelRepository;
import org.liara.api.data.repository.SensorRepository;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@UseSensorConfigurationOfType(MaxDurationSensorConfiguration.class)
@EmitStateOfType(LabelState.class)
@Component
@Scope("prototype")
public class MaxDurationSensor
  extends AbstractVirtualSensorHandler
{
  @NonNull
  private final SchemaManager _schemaManager;

  @NonNull
  private final LabelRepository _activations;

  @NonNull
  private final SensorRepository _sensors;

  @NonNull
  private final LabelRepository _activities;

  @Autowired
  public MaxDurationSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final LabelRepository activations,
    @NonNull final LabelRepository activities,
    @NonNull final SensorRepository sensors
  )
  {
    _schemaManager = schemaManager;
    _activations = activations;
    _sensors = sensors;
    _activities = activities;
  }

  public MaxDurationSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(MaxDurationSensorConfiguration.class);
  }

  public Sensor getSensor () {
    return getRunner().getSensor();
  }

  public Sensor getInputSensor () {
    return _sensors.getAt(getConfiguration().getInputSensor());
  }

  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);

    final List<LabelState> states = _activations.findWithDurationGreatherThan(
      getConfiguration().getInputSensor(),
                                                                                   getConfiguration().getMaxDuration()
    );

    final LabelStateCreationSchema schema = new LabelStateCreationSchema();

    for (final LabelState state : states) {
      schema.clear();
      schema.setStart(state.getStart());
      schema.setEnd(state.getEnd());
      schema.setTag(getConfiguration().getTag());
      schema.setEmittionDate(state.getEmittionDate());
      schema.setSensor(getSensor());
      schema.correlate("base", state);

      _schemaManager.execute(schema);
    }
  }

  @Override
  public void stateWasCreated (@NonNull final StateWasCreatedEvent event) {
    super.stateWasCreated(event);

    if (event.getState().getSensor().equals(getConfiguration().getInputSensor())) {
      final LabelStateSnapshot stateSnapshot = (LabelStateSnapshot) event.getState();

      if (Duration.between(stateSnapshot.getStart(), stateSnapshot.getEnd())
                  .compareTo(getConfiguration().getMaxDuration()) > 0) {
        emit(stateSnapshot.getModel());
      }
    }
  }

  @Override
  public void stateWasMutated (@NonNull final StateWasMutatedEvent event) {
    super.stateWasMutated(event);

    if (event.getNewValue().getSensor().equals(getConfiguration().getInputSensor())) {
      final LabelStateSnapshot oldSnapshot = (LabelStateSnapshot) event.getOldValue();
      final LabelStateSnapshot newSnapshot = (LabelStateSnapshot) event.getNewValue();

      final Duration oldDuration = Duration.between(oldSnapshot.getStart(), oldSnapshot.getEnd());
      final Duration newDuration = Duration.between(newSnapshot.getStart(), newSnapshot.getEnd());

      if ((oldDuration.compareTo(getConfiguration().getMaxDuration()) > 0) !=
          (newDuration.compareTo(getConfiguration().getMaxDuration()) > 0)) {
        if (newDuration.compareTo(getConfiguration().getMaxDuration()) > 0) {
          emit(newSnapshot.getModel());
        } else {
          delete(newSnapshot.getModel());
        }
      }
    }
  }

  @Override
  public void stateWillBeDeleted (@NonNull final StateWillBeDeletedEvent event) {
    final Optional<LabelState> activation = _activations.find(event.getState()
                                                                        .getState().as(LabelState.class));

    if (activation.isPresent() &&
        activation.get().getSensor().getReference().equals(getConfiguration().getInputSensor())) {
      final Duration duration = Duration.between(activation.get().getStart(), activation.get().getEnd());

      if (duration.compareTo(getConfiguration().getMaxDuration()) > 0) {
        delete(activation.get());
      }
    }
  }

  private void emit (@NonNull final LabelState state) {
    final LabelStateCreationSchema schema = new LabelStateCreationSchema();

    schema.setStart(state.getStart());
    schema.setEnd(state.getEnd());
    schema.setTag(getConfiguration().getTag());
    schema.setEmittionDate(state.getEmittionDate());
    schema.setSensor(getSensor());
    schema.correlate("base", state);

    _schemaManager.execute(schema);
  }

  private void delete (@NonNull final LabelState state) {
    final StateDeletionSchema schema = new StateDeletionSchema();

    schema.setState(_activities.findFirstWithCorrelation("base", state.getReference(), getSensor().getReference())
                               .get());

    _schemaManager.execute(schema);
  }
}
