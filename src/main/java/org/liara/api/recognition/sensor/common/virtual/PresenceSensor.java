package org.liara.api.recognition.sensor.common.virtual;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import org.liara.api.data.collection.ActivationStateCollection;
import org.liara.api.data.collection.EntityCollections;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivationStateCreationSchema;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.event.NodeWasCreatedEvent;
import org.liara.api.event.NodeWillBeCreatedEvent;
import org.liara.api.event.SensorWasCreatedEvent;
import org.liara.api.event.SensorWillBeCreatedEvent;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWillBeCreatedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

@EmitStateOfType(ActivationState.class)
public class PresenceSensor extends AbstractVirtualSensorHandler
{
  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final SchemaManager _schemaManager;
  
  @Nullable
  private ActivationStateCollection _states = null;
  
  @Autowired
  public PresenceSensor (
    @NonNull final EntityManager entityManager,
    @NonNull final SchemaManager schemaManager
  ) {
    _entityManager = entityManager;
    _schemaManager = schemaManager;
  }
  
  @Override
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);
    
    _states = EntityCollections.ACTIVATION_STATES.of(runner.getSensor());
  }

  @Override
  public void resume (@NonNull final VirtualSensorRunner runner) {
    super.resume(runner);
    
    _states = EntityCollections.ACTIVATION_STATES.of(runner.getSensor());
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
    
  }
  
  private void handleState (@NonNull final BooleanState state) {
    if (_states.getSize() <= 0) {
      initializePresenceCollection(state);
    } else {
      
    }
  }

  private void initializePresenceCollection (@NonNull final BooleanState state) {
    final ActivationStateCreationSchema schema = new ActivationStateCreationSchema();
    
    schema.setEmittionDate(state.getEmittionDate());
    schema.setStart(state.getEmittionDate());
    schema.setNode(state.getSensor().getNode());
    schema.setSensor(getRunner().getSensor());
    schema.setEnd(Optional.empty());
    
    _schemaManager.execute(schema);
  }

  @Override
  public void stop () {
    super.stop();
    
    _states = null;
  }

  @Override
  public void pause () {
    super.pause();
    
    _states = null;
  }
}
