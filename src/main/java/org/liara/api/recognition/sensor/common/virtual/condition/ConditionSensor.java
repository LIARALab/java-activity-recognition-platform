package org.liara.api.recognition.sensor.common.virtual.condition;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.*;
import org.liara.api.data.repository.TimeSeriesRepository;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeDeletedEvent;
import org.liara.api.event.StateWillBeMutatedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@UseSensorConfigurationOfType(ConditionSensorConfiguration.class)
@EmitStateOfType(BooleanState.class)
@Component
@Scope("prototype")
public abstract class ConditionSensor<CheckedState extends State>
  extends AbstractVirtualSensorHandler
{
  @NonNull
  private final SchemaManager _schemaManager;

  @NonNull
  private final TimeSeriesRepository<CheckedState> _input;

  @NonNull
  private final TimeSeriesRepository<BooleanState> _output;

  public ConditionSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final TimeSeriesRepository<CheckedState> data,
    @NonNull final TimeSeriesRepository<BooleanState> output
  )
  {
    super();
    _schemaManager = schemaManager;
    _input = data;
    _output = output;
  }

  public ConditionSensorConfiguration getConfiguration () {
    return getRunner().getSensor().getConfiguration().as(ConditionSensorConfiguration.class);
  }

  public ApplicationEntityReference<Sensor> getInputSensor () {
    return getConfiguration().getInputSensor();
  }

  public Sensor getSensor () {
    return getRunner().getSensor();
  }

  @Override
  public void initialize (
    @NonNull final VirtualSensorRunner runner
  )
  {
    super.initialize(runner);

    final List<CheckedState> states = _input.findAll(getInputSensor());

    if (states.size() > 0) {
      emit(states.get(0), check(states.get(0)));
    }

    for (int index = 1; index < states.size(); ++index) {
      if (check(states.get(index - 1)) != check(states.get(index))) {
        emit(states.get(index), check(states.get(index)));
      }
    }
  }

  @Override
  public void stateWasCreated (
    @NonNull final StateWasCreatedEvent event
  )
  {
    super.stateWasCreated(event);

    if (event.getState().getSensor() == getInputSensor()) {
      inputStateWasCreated((CheckedState) event.getState().getModel());
    }
  }

  /**
   * <table>
   *   <tr>
   *     <th>Previous</th>
   *     <th>Current</th>
   *     <th>Next</th>
   *     <th>Result</th>
   *   </tr>
   *   <tr><td>VRAI</td><td>VRAI</td><td>VRAI</td><!-- = --><td>NOTHING </td></tr>
   *   <tr><td>VRAI</td><td>VRAI</td><td>NULL</td><!-- = --><td>NOTHING </td></tr>
   *   <tr><td>FAUX</td><td>FAUX</td><td>FAUX</td><!-- = --><td>NOTHING</td></tr>
   *   <tr><td>FAUX</td><td>FAUX</td><td>NULL</td><!-- = --><td>NOTHING </td></tr>
   *   <tr><td>FAUX</td><td>FAUX</td><td>VRAI</td><!-- = --><td>NOTHING </td></tr>
   *   <tr><td>VRAI</td><td>VRAI</td><td>FAUX</td><!-- = --><td>NOTHING</td></tr>
   *   <tr><td>NULL</td><td>FAUX</td><td>VRAI</td><!-- = --><td>EMIT CURRENT </td></tr>
   *   <tr><td>NULL</td><td>FAUX</td><td>NULL</td><!-- = --><td>EMIT CURRENT </td></tr>
   *   <tr><td>NULL</td><td>VRAI</td><td>FAUX</td><!-- = --><td>EMIT CURRENT </td></tr>
   *   <tr><td>NULL</td><td>VRAI</td><td>NULL</td><!-- = --><td>EMIT CURRENT </td></tr>
   *   <tr><td>NULL</td><td>FAUX</td><td>FAUX</td><!-- = --><td>MOVE NEXT TO CURRENT </td></tr>
   *   <tr><td>NULL</td><td>VRAI</td><td>VRAI</td><!-- = --><td>MOVE NEXT TO CURRENT</td></tr>
   *   <tr><td>VRAI</td><td>FAUX</td><td>VRAI</td><!-- = --><td>EMIT CURRENT, EMIT NEXT </td></tr>
   *   <tr><td>FAUX</td><td>VRAI</td><td>FAUX</td><!-- = --><td>EMIT CURRENT, EMIT NEXT </td></tr>
   *   <tr><td>VRAI</td><td>FAUX</td><td>FAUX</td><!-- = --><td>MOVE NEXT TO CURRENT </td></tr>
   *   <tr><td>FAUX</td><td>VRAI</td><td>VRAI</td><!-- = --><td>MOVE NEXT TO CURRENT </td></tr>
   *   <tr><td>VRAI</td><td>FAUX</td><td>NULL</td><!-- = --><td>EMIT CURRENT </td></tr>
   *   <tr><td>FAUX</td><td>VRAI</td><td>NULL</td><!-- = --><td>EMIT CURRENT</td></tr>
   * </table>
   *
   * @param current
   */
  private void inputStateWasCreated (
    @NonNull final CheckedState current
  )
  {
    final Optional<CheckedState> previous = _input.findPrevious(current);
    final Optional<CheckedState> next     = _input.findNext(current);

    if (previous.isPresent()) {
      if (check(previous.get()) != check(current)) {
        if (!next.isPresent()) {
          emit(current, check(current));
        } else if (check(current) != check(next.get())) {
          emit(current, check(current));
          emit(next.get(), check(next.get()));
        } else {
          move(next.get(), current, check(current));
        }
      }
    } else if (!next.isPresent() || check(current) != check(next.get())) {
      emit(current, check(current));
    } else {
      move(next.get(), current, check(current));
    }
  }

  @Override
  public void stateWillBeMutated (
    @NonNull final StateWillBeMutatedEvent event
  )
  {
    super.stateWillBeMutated(event);
    final Optional<CheckedState> state = _input.find(event.getState().getState().getIdentifier());

    if (state.isPresent() && state.get().getSensor().equals(getInputSensor())) {
      inputStateWillBeMutated(state.get());
    }
  }

  private void inputStateWillBeMutated (@NonNull final CheckedState current) {
    final Optional<BooleanState> oldValue = _output.findFirstWithCorrelation(
      "origin",
      current.getReference(),
      getSensor().getReference()
    );

    if (oldValue.isPresent()) {
      delete(oldValue.get());
    }
  }

  @Override
  public void stateWasMutated (
    @NonNull final StateWasMutatedEvent event
  )
  {
    super.stateWasMutated(event);

    if (event.getNewValue().getSensor() == getInputSensor()) {
      inputStateWasCreated((CheckedState) event.getNewValue().getModel());
    }
  }

  @Override
  public void stateWillBeDeleted (
    @NonNull final StateWillBeDeletedEvent event
  )
  {
    super.stateWillBeDeleted(event);

    final Optional<CheckedState> state = _input.find(event.getState().getState().getIdentifier());

    if (state.isPresent() && state.get().getSensorIdentifier().equals(getInputSensor().getIdentifier())) {
      inputStateWillBeDeleted(state.get());
    }
  }

  /**
   * <table>
   *   <tr>
   *     <th>Previous</th>
   *     <th>Current</th>
   *     <th>Next</th>
   *     <th>Result</th>
   *   </tr>
   *   <tr><td>VRAI</td><td>VRAI</td><td>VRAI</td><!-- = --><td>NOTHING</td></tr>
   *   <tr><td>VRAI</td><td>VRAI</td><td>NULL</td><!-- = --><td>NOTHING</td></tr>
   *   <tr><td>FAUX</td><td>FAUX</td><td>FAUX</td><!-- = --><td>NOTHING</td></tr>
   *   <tr><td>FAUX</td><td>FAUX</td><td>NULL</td><!-- = --><td>NOTHING</td></tr>
   *   <tr><td>FAUX</td><td>FAUX</td><td>VRAI</td><!-- = --><td>NOTHING</td></tr>
   *   <tr><td>VRAI</td><td>VRAI</td><td>FAUX</td><!-- = --><td>NOTHING</td></tr>
   *   <tr><td>NULL</td><td>FAUX</td><td>VRAI</td><!-- = --><td>DELETE CURRENT</td></tr>
   *   <tr><td>NULL</td><td>FAUX</td><td>NULL</td><!-- = --><td>DELETE CURRENT</td></tr>
   *   <tr><td>NULL</td><td>VRAI</td><td>FAUX</td><!-- = --><td>DELETE CURRENT</td></tr>
   *   <tr><td>NULL</td><td>VRAI</td><td>NULL</td><!-- = --><td>DELETE CURRENT</td></tr>
   *   <tr><td>NULL</td><td>FAUX</td><td>FAUX</td><!-- = --><td>MOVE CURRENT TO NEXT</td></tr>
   *   <tr><td>NULL</td><td>VRAI</td><td>VRAI</td><!-- = --><td>MOVE CURRENT TO NEXT</td></tr>
   *   <tr><td>VRAI</td><td>FAUX</td><td>VRAI</td><!-- = --><td>DELETE CURRENT, DELETE NEXT</td></tr>
   *   <tr><td>FAUX</td><td>VRAI</td><td>FAUX</td><!-- = --><td>DELETE CURRENT, DELETE NEXT</td></tr>
   *   <tr><td>VRAI</td><td>FAUX</td><td>FAUX</td><!-- = --><td>MOVE CURRENT TO NEXT</td></tr>
   *   <tr><td>FAUX</td><td>VRAI</td><td>VRAI</td><!-- = --><td>MOVE CURRENT TO NEXT</td></tr>
   *   <tr><td>VRAI</td><td>FAUX</td><td>NULL</td><!-- = --><td>DELETE CURRENT</td></tr>
   *   <tr><td>FAUX</td><td>VRAI</td><td>NULL</td><!-- = --><td>DELETE CURRENT</td></tr>
   * </table>
   *
   * @param current
   */
  private void inputStateWillBeDeleted (
    @NonNull final CheckedState current
  )
  {
    final Optional<CheckedState> previous = _input.findPrevious(current);
    final Optional<CheckedState> next     = _input.findNext(current);

    if (previous.isPresent()) {
      if (check(previous.get()) != check(current)) {
        if (!next.isPresent()) {
          delete(current);
        } else if (check(current) != check(next.get())) {
          delete(current);
          delete(next.get());
        } else {
          move(current, next.get(), check(current));
        }
      }
    } else if (!next.isPresent() || check(current) != check(next.get())) {
      delete(current);
    } else {
      move(current, next.get(), check(current));
    }
  }

  protected abstract boolean check (@NonNull final CheckedState state);

  private void delete (@NonNull final CheckedState correlated) {
    final BooleanState toDelete = _output.findFirstWithCorrelation("origin",
      correlated.getReference(),
      getSensor().getReference()
    ).get();

    delete(toDelete);
  }

  private void delete (@NonNull final BooleanState correlated) {
    final StateDeletionSchema deletion = new StateDeletionSchema();
    deletion.setState(correlated);
    _schemaManager.execute(deletion);
  }

  private void emit (
    @NonNull final CheckedState state, @NonNull final boolean up
  )
  {
    final BooleanStateCreationSchema creation = new BooleanStateCreationSchema();
    creation.setSensor(getSensor());
    creation.setEmittionDate(state.getEmittionDate());
    creation.setValue(up);
    creation.correlate("origin", state);

    _schemaManager.execute(creation);
  }

  private void move (
    @NonNull final CheckedState from, @NonNull final CheckedState to, @NonNull final boolean up
  )
  {
    final BooleanState toMove = _output.findFirstWithCorrelation("origin",
      from.getReference(),
      getSensor().getReference()
    ).get();

    final BooleanStateMutationSchema mutation = new BooleanStateMutationSchema();

    mutation.setState(toMove);
    mutation.setEmittionDate(to.getEmittionDate());
    mutation.setValue(up);
    mutation.correlate("origin", to);

    _schemaManager.execute(mutation);
  }
}
