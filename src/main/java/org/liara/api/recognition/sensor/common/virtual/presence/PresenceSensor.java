package org.liara.api.recognition.sensor.common.virtual.presence;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Objects;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivationStateCreationSchema;
import org.liara.api.data.entity.state.ActivationStateMutationSchema;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateDeletionSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeMutatedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.configuration.PresenceSensorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@UseSensorConfigurationOfType(PresenceSensorConfiguration.class)
@EmitStateOfType(ActivationState.class)
@Component
@Scope("prototype")
public class PresenceSensor extends AbstractVirtualSensorHandler
{   
  @NonNull
  private final SchemaManager _schemaManager;
  
  @NonNull
  private final PresenceSensorData _data;
  
  @Autowired
  public PresenceSensor (
    @NonNull final SchemaManager schemaManager,
    @NonNull final PresenceSensorData data
  ) {
    _schemaManager = schemaManager;
    _data = data;
  }
  
  private Sensor getSensor () {
    return getRunner().getSensor();
  }
  
  private PresenceSensorConfiguration getConfiguration () {
    return getSensor().getConfiguration()
                      .as(PresenceSensorConfiguration.class);
  }
  
  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);
        
    final Iterator<BooleanState> ticks = _data.getTrackedMotionActivation(runner.getSensor()).iterator();
    
    ActivationState currentState = null;
    BooleanState previousTick = null;
    
    while (ticks.hasNext()) {
      currentState = didInitializationTickDiscovered(
        currentState, previousTick, previousTick = ticks.next()
      );
    }
  }

  private ActivationState didInitializationTickDiscovered (
    @NonNull final ActivationState currentState,
    @NonNull final BooleanState previousTick,
    @NonNull final BooleanState discovered
  ) {
    if (currentState == null) {
      return handlePresenceCreation(discovered, null, discovered.getNodeIdentifier());
    } else if (Objects.equals(currentState.getNodeIdentifier(), discovered.getNodeIdentifier())) {
      return checkOutdoor(previousTick, discovered, currentState);
    } else {
      handlePresenceMerging(currentState, discovered, discovered.getEmittionDate());
      checkOutdoor(previousTick, discovered, currentState);
      return handlePresenceCreation(discovered, null, discovered.getNodeIdentifier());
    }
  }

  @Override
  public void stateWasCreated (@NonNull final StateWasCreatedEvent event) {
    final State state = event.getState().getModel();
    
    if (isValidInputTick(state)) handleTickDiscovery((BooleanState) state); 
  }

  private boolean isValidInputTick (@NonNull final State state) {
    return state instanceof BooleanState &&
           ((BooleanState) state).getValue() == true &&
           _data.isTracked(getSensor(), state);
  }
  
  private void handleTickDiscovery (@NonNull final BooleanState tick) {    
    final ActivationState before = _data.getActivationBefore(
      getSensor(), 
      tick.getEmittionDate()
    );
    
    final ActivationState after = _data.getActivationAfter(
      getSensor(), 
      tick.getEmittionDate()
    );
    
    if (before == null && after == null) {
      handleEmptyCollectionSplit(tick);
    } else if (before == null && after != null) {
      handleLeftCollectionSplit(tick, after);
    } else if (before != null && after == null) {
      handleRightCollectionSplit(before, tick);
    } else if (before.getIdentifier().equals(after.getIdentifier())) {
      handleTwinCollectionSplit(tick, before);
    } else {
      handleMiddleCollectionSplit(before, tick, after);
    }
  }
  
  private void handleTwinCollectionSplit (
    @NonNull final BooleanState tick, 
    @NonNull final ActivationState twin
  ) {
    System.out.println("TWIN" + tick.getIdentifier());
    /*
    if (tick.getSensor().getNodeIdentifier().equals(twin.getNodeIdentifier())) return;
    
    final ActivationStateCreationSchema creation = new ActivationStateCreationSchema();
    
    creation.setEmittionDate(tick.getEmittionDate());
    creation.setNode(tick.getSensor().getNode());
    creation.setStartState(tick);
    creation.setEndState(twin.getStartState());
    creation.setSensor(getSensor());
    
    _schemaManager.execute(creation);
    */
  }

  /**
   * Called when a tick is discovered with activation states before and after it.
   * 
   * ---][ActivationState---------][ActivationState---X(DiscoveredTick)-][ActivationState---------
   * 
   * This method will do :
   *   - Nothing, if the given discovered tick sensor's node is the same as the
   *     declared note of its previous activation state.
   *     
   *   - Merge the tick with its next activation state it no ticks exists between
   *     the discovered tick and the tick that start at the next activation state.
   *     
   *   - Create a new Activation state that start at the given tick and stop
   *     at its immediate next tick and create a new Activation state that start at
   *     the immediate next tick and stop at the beginning of the next activation state
   *     otherwise.
   * 
   * @param before Activation state before the given tick.
   * @param tick Discovered tick instance.
   * @param after Activation state after the given tick.
   */
  private void handleMiddleCollectionSplit (
    @NonNull final ActivationState before, 
    @NonNull final BooleanState tick, 
    @NonNull final ActivationState after
  ) {
    if (Objects.equals(tick.getNodeIdentifier(), before.getNodeIdentifier())) {
      handleIgnoredTick(tick, before);
    } else if (Objects.equals(before.getNodeIdentifier(), getConfiguration().getOutdoorNode())) {
      handleIgnoredTick(tick, forgetOutdoor(before));
    } else if (
      Objects.equals(
        _data.getActivationBefore(getSensor(), before.getStart()).getNodeIdentifier(),
        getConfiguration().getOutdoorNode()
      )
    ) {
      handleIgnoredTick(tick, forgetOutdoor(_data.getActivationBefore(getSensor(), before.getStart())));
    } else {
      BooleanState nextTick = _data.getTickAfter(getSensor(), tick.getEmittionDate());
      
      // Twin ticks
      if (Objects.equals(nextTick.getEmittionDate(), after.getStart())) { 
        nextTick = (BooleanState) after.getStartState(); 
      }
          
      // merge
      if (
        Objects.equals(nextTick.getIdentifier(), after.getStartStateIdentifier()) &&
        Objects.equals(tick.getNodeIdentifier(), after.getNodeIdentifier())
      ) {
        handlePresenceMerging(tick, after);
        handlePresenceMerging(before, tick);
      }
      // split
      else {
        handlePresenceCreation(tick, nextTick, tick.getSensor().getNode());
        handlePresenceMerging(before, tick);
        
        // two split
        if (!Objects.equals(nextTick.getIdentifier(), after.getStartStateIdentifier())) {
          handlePresenceCreation(nextTick, after.getStartState(), nextTick.getSensor().getNode());
        }
      }
    }
  }

  private ActivationState forgetOutdoor (@NonNull final ActivationState outdoor) {
    final ActivationState before = _data.getActivationBefore(getSensor(), outdoor.getStart());
    final ActivationState after = _data.getActivationAfter(getSensor(), outdoor.getEnd());
    
    final ActivationStateMutationSchema mutation = new ActivationStateMutationSchema();
    mutation.setIdentifier(before.getIdentifier());
    mutation.setEnd(after.getEnd());
    mutation.correlate("end", after.getEndState());
    
    _schemaManager.execute(new StateDeletionSchema(outdoor));
    _schemaManager.execute(new StateDeletionSchema(after));
    _schemaManager.execute(mutation);
   
    return before;
  }

  /**
   * Called when a tick is discovered with activation states before it and without any activation state after it.
   * 
   * ---][ActivationState---------][ActivationState---------X(DiscoveredTick)----------------
   * 
   * 
   * This method can do :
   *   - Nothing if the immediate activation state before the discovered tick
   *     declare an active node that is equal to the tick sensor's node.
   *     
   *   - Terminate the previous activation state at the tick otherwise and 
   *     then create a new activation state that start at the tick and :
   *      - Does not finish if the discovered tick is the latest discovered tick.
   *      - Otherwise does finish at the immediate tick after the discovered tick, and create
   *        a new unfinished activation state that begin at this next tick.
   * 
   * @param before Activation state immediatly before the discovered tick.
   * @param tick The discovered tick instance.
   */
  private void handleRightCollectionSplit (
    @NonNull final ActivationState before, 
    @NonNull final BooleanState tick
  ) {
    // The previous presence is extended but not ended
    if (
      Objects.equals(
        before.getNodeIdentifier(),
        tick.getNodeIdentifier()
      )
    ) {
      handleIgnoredTick(tick, before);
    } else {
      final BooleanState nextTick = _data.getTickAfter(getSensor(), tick.getEmittionDate());
      
      handlePresenceMerging(before, tick);
      handlePresenceCreation(
        tick, nextTick, 
        tick.getSensor().getNode()
      );
      
      if (nextTick != null) {
        handlePresenceCreation(
          nextTick, null, 
          nextTick.getSensor().getNode()
        );
      }
    }
  }

  /**
   * Called when a tick is discovered with activation states after it and without any activation state before it.
   * 
   * This method can do two things :
   *   - If the immediate activation state after the discovered tick
   *     declare an active node that is equal to the tick sensor's node, 
   *     this method will merge the tick with the next activation state.
   *     (The next state will be updated in order to start at the discovered
   *     tick) .
   *     
   *   - Otherwise, this method will create a new activation state that begin
   *     at the discovered tick and that finish at the start of the next activation 
   *     state. The created activation state will declare as an active node the
   *     node that store the sensor of the discovered tick.
   *     
   * -------X(DiscoveredTick)------------[ActivationState---------][ActivationState------------
   * 
   * @param tick Discovered tick instance.
   * @param next Activation state immediatly after the discovered tick.
   */
  private void handleLeftCollectionSplit (
    @NonNull final BooleanState tick,
    @NonNull final ActivationState next
  ) {
    // Merging when both tick and next activation state target the same node.
    if (
      Objects.equals(
        next.getNodeIdentifier(),
        tick.getNodeIdentifier()
      )
    ) {
      handlePresenceMerging(tick, next);
    } 
    // Inserting a new presence when next activation state and discovered tick does not target the same node.
    else {
      handlePresenceCreation(
        tick, next.getStartState(), 
        tick.getSensor().getNode()
      );
    }
  }

  /**
   * Called when a tick is discovered without any activation state before it, and without any activation state after it.
   * 
   * This method will create activation that begins at the discovered tick and does not finish.
   * 
   * -------------------------X(DiscoveredTick)------------------------
   * -------------------------[ActivationState-------------------------
   * 
   * @param tick Discovered tick.
   */
  private void handleEmptyCollectionSplit (
    @NonNull final State tick
  ) {
    handlePresenceCreation(
      tick, null, 
      tick.getSensor().getNode()
    );
  }
  
  private void handlePresenceMerging (
    @NonNull final State startState,
    @NonNull final ActivationState next
  ) {    
    handlePresenceMerging(startState, startState.getEmittionDate(), next);
    checkOutdoorRight(startState, next);
  }

  private void handlePresenceMerging (
    @NonNull final State startState,
    @NonNull final ZonedDateTime start,
    @NonNull final ActivationState next
  ) {
    final ActivationStateMutationSchema mutation = new ActivationStateMutationSchema();
    
    mutation.setIdentifier(next);
    mutation.setStart(start);
    mutation.correlate("start", startState);
    mutation.setEmittionDate(startState.getEmittionDate());
    
    _schemaManager.execute(mutation);
  }
  
  private void handlePresenceMerging (
    @NonNull final ActivationState previous,
    @NonNull final State endState
  ) {    
    handlePresenceMerging(previous, endState, endState.getEmittionDate());
    checkOutdoorLeft(endState, previous);
  }
  
  private void handlePresenceMerging (
    @NonNull final ActivationState previous,
    @NonNull final State endState,
    @NonNull final ZonedDateTime end
  ) {
    final ActivationStateMutationSchema mutation = new ActivationStateMutationSchema();
    
    mutation.setIdentifier(previous);
    mutation.setEnd(end);
    mutation.correlate("end", endState);
    
    _schemaManager.execute(mutation);
  }

  private void handlePresenceCreation (
    @NonNull final State start, 
    @NonNull final State end,
    @NonNull final Node node
  ) {
    handlePresenceCreation(
      start, 
      end, 
      node == null ? null : node.getIdentifier()
    );
  }
  
  private ActivationState handlePresenceCreation (
    @NonNull final State start, 
    @NonNull final State end,
    @NonNull final Long node
  ) {
    ActivationState result = handlePresenceCreation(
      start, end, 
      start.getEmittionDate(), end == null ? null : end.getEmittionDate(), 
      node
    );
    
    // @TODO double check
    if (end != null) {
      result = checkOutdoorLeft(end, result);
    }
    
    return result;
  }
  
  private ActivationState handlePresenceCreation (
    @NonNull final State startState, 
    @NonNull final State endState,
    @NonNull final ZonedDateTime start,
    @NonNull final ZonedDateTime end,
    @NonNull final Long node
  ) {
    final ActivationStateCreationSchema creation = new ActivationStateCreationSchema();
    
    creation.setEmittionDate(start);
    creation.setStart(start);
    creation.correlate("start", startState);
    
    if (end != null) {
      creation.setEnd(end);
      creation.correlate("end", endState);
    }
    
    creation.setNode(node);
    creation.setSensor(getSensor());
    
    return _schemaManager.execute(creation);
  }

  private ActivationState handleIgnoredTick (
    @NonNull final BooleanState tick, 
    @NonNull final ActivationState container
  ) {
    final ActivationState nextContainer;
    
    if (isPotentiallyOutdoor(container)) {
      nextContainer = checkOutdoorLeft(tick, container);
    } else {
      nextContainer = container;
    }
    
    if (isPotentiallyOutdoor(tick)) {
      return checkOutdoorRight(tick, nextContainer);
    } else {
      return nextContainer; 
    }
  }

  private ActivationState checkOutdoorRight (
    @NonNull final State tick, 
    @NonNull final ActivationState container
  ) {
    if (!isPotentiallyOutdoor(tick)) return container;
    
    final BooleanState next = _data.getTickAfter(getSensor(), tick.getEmittionDate());
    
    return checkOutdoor(tick, next, container);
  }

  private ActivationState checkOutdoorLeft (
    @NonNull final State tick, 
    @NonNull final ActivationState container
  ) {
    final BooleanState previous = _data.getTickBefore(getSensor(), tick.getEmittionDate());
    if (!isPotentiallyOutdoor(previous)) { return container; }
    
    return checkOutdoor(previous, tick, container);
  }

  private ActivationState checkOutdoor (
    @NonNull final State start, 
    @NonNull final State end, 
    @NonNull final ActivationState container
  ) {
    if (!isPotentiallyOutdoor(start)) { return container; }
    
    if (
      end == null ||
      Duration.between(start.getEmittionDate(), end.getEmittionDate())
              .compareTo(Duration.ofMinutes(15)) < 0
    ) { return container; }
    
    final State containerEnd = container.getEndState();
    
    handlePresenceMerging(
      container, start, start.getEmittionDate().plusMinutes(2)
    );
    
    handlePresenceCreation(
      start, end, 
      start.getEmittionDate().plusMinutes(2), end.getEmittionDate().minusMinutes(2), 
      getConfiguration().getOutdoorNode()
    );
    
    if (Objects.equals(end, containerEnd)) {
      return handlePresenceCreation(
        start, end, 
        end.getEmittionDate().minusMinutes(2), 
        end.getEmittionDate(),
        container.getNodeIdentifier()
      );
    } else {
      return handlePresenceCreation(
        end, 
        containerEnd, 
        end.getEmittionDate().minusMinutes(2), 
        end == null ? null : end.getEmittionDate(),
        container.getNodeIdentifier()
      );
    }
  }

  private boolean isPotentiallyOutdoor (@NonNull final State tick) {
    return getConfiguration().isPotentiallyOutdoorNode(tick.getNodeIdentifier());
  }

  @Override
  public void stateWillBeMutated (@NonNull final StateWillBeMutatedEvent event) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stateWasMutated (@NonNull final StateWasMutatedEvent event) {
    // TODO Auto-generated method stub
    
  }
}
