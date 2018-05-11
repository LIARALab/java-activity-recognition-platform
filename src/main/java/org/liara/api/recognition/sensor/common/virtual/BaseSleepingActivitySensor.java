package org.liara.api.recognition.sensor.common.virtual;

import java.time.Duration;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import org.liara.api.collection.Operators;
import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.data.collection.ActivationStateCollection;
import org.liara.api.data.collection.BaseSleepingActivityStateCollection;
import org.liara.api.data.collection.EntityCollections;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.activity.BaseSleepingActivityMutationSchema;
import org.liara.api.data.entity.state.activity.BaseSleepingActivityState;
import org.liara.api.data.entity.state.activity.BaseSleepingActivityStateCreationSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.NodeWasCreatedEvent;
import org.liara.api.event.NodeWillBeCreatedEvent;
import org.liara.api.event.SensorWasCreatedEvent;
import org.liara.api.event.SensorWillBeCreatedEvent;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWasMutatedEvent;
import org.liara.api.event.StateWillBeCreatedEvent;
import org.liara.api.event.StateWillBeMutatedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.configuration.BaseSleepingActivitySensorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@UseSensorConfigurationOfType(BaseSleepingActivitySensorConfiguration.class)
@EmitStateOfType(BaseSleepingActivityState.class)
@Component
@Scope("prototype")
public class BaseSleepingActivitySensor extends AbstractVirtualSensorHandler
{
  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final SchemaManager _schemaManager;
  
  @Nullable
  private ActivationStateCollection _watched = null;
  
  @Nullable
  private BaseSleepingActivityStateCollection _states = null;
  
  @Autowired
  public BaseSleepingActivitySensor (
    @NonNull final EntityManager entityManager,
    @NonNull final SchemaManager schemaManager
  ) {
    _entityManager = entityManager;
    _schemaManager = schemaManager;
  }
  
  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);
        
    initializeTrackedCollections();    
    initializeSensorCollection();
  }

  private void initializeSensorCollection () {
    final BaseSleepingActivityStateCreationSchema creationSchema = new BaseSleepingActivityStateCreationSchema();
    final List<ActivationState> presences = getInitialisationStates();
    int presenceCount = presences.size();
    int executedSchemas = 0;
    
    for (int index = 0; index < presenceCount; ++index) {
      final ActivationState next = presences.get(index);
      
      if (next.getDuration().compareTo(Duration.ofHours(1)) >= 0) {
        creationSchema.clear();
        creationSchema.setEmittionDate(next.getEmittionDate().plusMinutes(15));
        creationSchema.setStart(next.getStart().plusMinutes(15));
        creationSchema.setEnd(next.getEnd().minusMinutes(15));
        creationSchema.setRelatedPresence(next);
        creationSchema.setTag("SLEEPING");
        creationSchema.setNode(getRunner().getSensor().getNode());
        creationSchema.setSensor(getRunner().getSensor());
        
        _schemaManager.execute(creationSchema);
        executedSchemas += 1;
        
        if (executedSchemas >= 20) {
          executedSchemas = 0;
          _entityManager.flush();
          _entityManager.clear();
        }
      }
    }
  }
  
  private List<ActivationState> getInitialisationStates () {
    final EntityCollectionMainQuery<ActivationState, ActivationState> query = _watched.apply(
      Operators.orderAscendingBy(x -> x.get("_emittionDate"))
    ).createCollectionQuery();
    
    query.join(x -> x.join("_sensor")).join(x -> x.join("_node"));
    
    final List<ActivationState> result = _entityManager.createQuery(query.getCriteriaQuery())
                                                       .getResultList();
    
    _entityManager.clear();
    
    return result;
  }

  @Override
  public void resume (@NonNull final VirtualSensorRunner runner) {
    super.resume(runner);
    
    initializeTrackedCollections();
  }
  
  private void initializeTrackedCollections () {
    _states = EntityCollections.BASE_SLEEPING_ACTIVITIES.of(getRunner().getSensor());
    _watched = EntityCollections.ACTIVATION_STATES.of(
      EntityCollections.SENSORS.findByIdentifier(
        getRunner().getSensor().getConfiguration()
                   .as(BaseSleepingActivitySensorConfiguration.class)
                   .getSourcePresenceSensorIdentifier()
      ).get()
    ).presences().of(getRunner().getSensor().getNode());
  }
  
  @Override
  public void sensorWillBeCreated (@NonNull final SensorWillBeCreatedEvent event) {
    
  }

  @Override
  public void sensorWasCreated (@NonNull final SensorWasCreatedEvent event) {
    
  }

  @Override
  public void nodeWillBeCreated (@NonNull final NodeWillBeCreatedEvent event) {
    
  }

  @Override
  public void nodeWasCreated (@NonNull final NodeWasCreatedEvent event) {
    
  }

  @Override
  public void stateWillBeCreated (@NonNull final StateWillBeCreatedEvent event) {
    
  }

  @Override
  public void stateWasCreated (@NonNull final StateWasCreatedEvent event) {
    final State state = event.getState().getModel();
    
    if (isValidInputState(state)) handleStateDiscovery((ActivationState) state);
  }

  private boolean isValidInputState (@NonNull final State state) {
    if (!(state instanceof ActivationState)) return false;
    
    final ActivationState activationState = (ActivationState) state;
        
    return activationState.getNode().getType().equals("common/room") &&
           activationState.getNodeIdentifier().equals(getRunner().getSensor().getNodeIdentifier()) &&
           activationState.getSensorIdentifier().equals(
             getRunner().getSensor().getConfiguration()
                        .as(BaseSleepingActivitySensorConfiguration.class)
                        .getSourcePresenceSensorIdentifier()
           );
  }
  
  private void handleStateDiscovery (@NonNull final ActivationState state) {
    final Duration stateDuration = state.getDuration();
    
    if (stateDuration == null || stateDuration.compareTo(Duration.ofHours(1)) < 0) return;
    
    final BaseSleepingActivityStateCreationSchema creationSchema = new BaseSleepingActivityStateCreationSchema();
    
    creationSchema.setEmittionDate(state.getEmittionDate().plusMinutes(15));
    creationSchema.setStart(state.getStart().plusMinutes(15));
    creationSchema.setEnd(state.getEnd().minusMinutes(15));
    creationSchema.setRelatedPresence(state);
    creationSchema.setTag("SLEEPING");
    creationSchema.setNode(getRunner().getSensor().getNode());
    creationSchema.setSensor(getRunner().getSensor());
    
    _schemaManager.execute(creationSchema);
  }

  @Override
  public void stateWillBeMutated (@NonNull final StateWillBeMutatedEvent event) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stateWasMutated (@NonNull final StateWasMutatedEvent event) {
    final State state = event.getState().getModel();
    
    if (isValidInputState(state)) handleStateMutation((ActivationState) state);
  }

  private void handleStateMutation (@NonNull final ActivationState state) {
    final BaseSleepingActivityStateCollection related = _states.apply((query) -> {
      query.andWhere(query.getEntity().get("_relatedPresence").in(state));
    });
    
    if (related.getSize() > 0) {
      handleStateMutation(related.get(0), state);
    } else {
      handleStateDiscovery(state);
    }
  }

  private void handleStateMutation (
    @NonNull final BaseSleepingActivityState related, 
    @NonNull final ActivationState muted
  ) {
    final Duration stateDuration = muted.getDuration();
    
    if (stateDuration == null || stateDuration.compareTo(Duration.ofHours(1)) < 0) {
      /** @TODO deletion */
      related.delete();
      _entityManager.merge(related);
    } else {
      final BaseSleepingActivityMutationSchema mutation = new BaseSleepingActivityMutationSchema();
      
      mutation.setEmittionDate(muted.getEmittionDate().plusMinutes(15));
      mutation.setStart(muted.getStart().plusMinutes(15));
      mutation.setEnd(muted.getEnd().minusMinutes(15));
      mutation.setRelatedPresence(muted);
      
      _schemaManager.execute(mutation);
    }
  }

  @Override
  public void stop () {
    super.stop();
    
    _states = null;
    _watched = null;
  }

  @Override
  public void pause () {
    super.pause();
    
    _states = null;
    _watched = null;
  }
}
