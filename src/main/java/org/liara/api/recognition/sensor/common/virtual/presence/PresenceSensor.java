package org.liara.api.recognition.sensor.common.virtual.presence;

import java.util.Iterator;
import java.util.Optional;

import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivationStateCreationSchema;
import org.liara.api.data.entity.state.ActivationStateMutationSchema;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeMutatedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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
  
  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);
        
    final ActivationStateCreationSchema creationSchema = new ActivationStateCreationSchema();
    final Iterator<BooleanState> ticks = _data.getTrackedMotionActivation(runner.getSensor()).iterator();
    
    if (ticks.hasNext()) {
      BooleanState last = ticks.next();
      
      while (ticks.hasNext()) {
        final BooleanState next = ticks.next();
        
        if (!last.getSensor().getNodeIdentifier().equals(next.getSensor().getNodeIdentifier())) {
          creationSchema.clear();
          creationSchema.setEmittionDate(last.getEmittionDate());
          creationSchema.setStartState(last);
          creationSchema.setEndState(next);
          creationSchema.setNode(last.getSensor().getNode());
          creationSchema.setSensor(getRunner().getSensor());
          
          _schemaManager.execute(creationSchema);
          
          last = next;
        }
      }
      
      creationSchema.clear();
      creationSchema.setEmittionDate(last.getEmittionDate());
      creationSchema.setStartState(last);
      creationSchema.setEndState(Optional.empty());
      creationSchema.setNode(last.getSensor().getNode());
      creationSchema.setSensor(getRunner().getSensor());
      
      _schemaManager.execute(creationSchema);
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
           _data.isTracked(getRunner().getSensor(), state);
  }
  
  private void handleTickDiscovery (@NonNull final BooleanState tick) {    
    final ActivationState before = _data.getActivationBefore(
      getRunner().getSensor(), 
      tick.getEmittionDate()
    );
    
    final ActivationState after = _data.getActivationAfter(
      getRunner().getSensor(), 
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
    creation.setSensor(getRunner().getSensor());
    
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
    //System.out.println("MIDDLE" + tick.getIdentifier());
    if (tick.getSensor().getNodeIdentifier().equals(before.getNodeIdentifier())) return;
    
    final ActivationStateMutationSchema mutation = new ActivationStateMutationSchema();
    final ActivationStateCreationSchema creation = new ActivationStateCreationSchema();
    BooleanState nextTick = _data.getTickAfter(getRunner().getSensor(), tick.getEmittionDate());
    
    // Twin ticks
    if (nextTick.getEmittionDate().equals(after.getStartState().getEmittionDate())) { 
      nextTick = (BooleanState) after.getStartState(); 
    }
        
    
    // merge
    if (
      nextTick.getIdentifier().equals(after.getStartStateIdentifier()) &&
      tick.getSensor().getNodeIdentifier().equals(after.getNodeIdentifier())
    ) {
      mutation.setIdentifier(after);
      mutation.setStartState(tick);
      mutation.setEmittionDate(tick.getEmittionDate());
      
      _schemaManager.execute(mutation);
      
      mutation.clear();
      mutation.setIdentifier(before);
      mutation.setEndState(tick);
      
      _schemaManager.execute(mutation);
    }
    // split
    else {
      creation.setStartState(tick);
      creation.setEmittionDate(tick.getEmittionDate());
      creation.setEndState(nextTick);
      creation.setSensor(getRunner().getSensor());
      creation.setNode(tick.getSensor().getNode());
      
      _schemaManager.execute(creation);
      
      mutation.setIdentifier(before);
      mutation.setEndState(tick);
      
      _schemaManager.execute(mutation);
      
      // two split
      if (!nextTick.getIdentifier().equals(after.getStartStateIdentifier())) {
        creation.clear();
        
        creation.setStartState(nextTick);
        creation.setEmittionDate(nextTick.getEmittionDate());
        creation.setEndState(after.getStartState());
        creation.setSensor(getRunner().getSensor());
        creation.setNode(nextTick.getSensor().getNode());
        
        _schemaManager.execute(creation);
      }
    }
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
    //System.out.println("RIGHT" + tick.getIdentifier());
    if (before.getNodeIdentifier().equals(tick.getSensor().getNodeIdentifier())) return;
    
    final ActivationStateMutationSchema mutation = new ActivationStateMutationSchema();
    final ActivationStateCreationSchema creation = new ActivationStateCreationSchema();
    final BooleanState nextTick = _data.getTickAfter(getRunner().getSensor(), tick.getEmittionDate());
    
    mutation.setIdentifier(before);
    mutation.setEndState(tick);
    
    _schemaManager.execute(mutation);
    
    creation.setEmittionDate(tick.getEmittionDate());
    creation.setStartState(tick);
    creation.setSensor(getRunner().getSensor());
    creation.setNode(tick.getSensor().getNode());
    creation.setEndState(nextTick);
    
    _schemaManager.execute(creation);
    
    if (nextTick != null) {
      creation.clear();
      creation.setEmittionDate(nextTick.getEmittionDate());
      creation.setStartState(nextTick);
      creation.setSensor(getRunner().getSensor());
      creation.setNode(nextTick.getSensor().getNode());
      
      _schemaManager.execute(creation);
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
    //System.out.println("LEFT " + tick.getIdentifier());
    // Merging
    if (next.getNodeIdentifier().equals(tick.getSensor().getNodeIdentifier())) {
      final ActivationStateMutationSchema mutation = new ActivationStateMutationSchema();
      
      mutation.setIdentifier(next);
      mutation.setStartState(tick);
      mutation.setEmittionDate(tick.getEmittionDate());
      
      _schemaManager.execute(mutation);
    } 
    // Inserting
    else {
      final ActivationStateCreationSchema creation = new ActivationStateCreationSchema();
      
      creation.setEmittionDate(tick.getEmittionDate());
      creation.setStartState(tick);
      creation.setEndState(next.getStartState());
      creation.setNode(tick.getSensor().getNode());
      creation.setSensor(getRunner().getSensor());
      
      _schemaManager.execute(creation);
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
    @NonNull final BooleanState tick
  ) {
    final ActivationStateCreationSchema creation = new ActivationStateCreationSchema();
    
    creation.setEmittionDate(tick.getEmittionDate());
    creation.setStartState(tick);
    creation.setEndState(Optional.empty());
    creation.setNode(tick.getSensor().getNode());
    creation.setSensor(getRunner().getSensor());
    
    _schemaManager.execute(creation);
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
