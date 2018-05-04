package org.liara.api.recognition.sensor.common.virtual;

import java.util.Iterator;

import javax.transaction.Transactional;

import org.liara.api.collection.Operators;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.DoubleState;
import org.liara.api.data.entity.state.State;
import org.liara.api.event.NodeWasCreatedEvent;
import org.liara.api.event.NodeWillBeCreatedEvent;
import org.liara.api.event.SensorWasCreatedEvent;
import org.liara.api.event.SensorWillBeCreatedEvent;
import org.liara.api.event.StateWasCreatedEvent;
import org.liara.api.event.StateWillBeCreatedEvent;
import org.liara.api.recognition.sensor.AbstractVirtualSensorHandler;
import org.liara.api.recognition.sensor.EmitStateOfType;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.liara.api.recognition.sensor.VirtualSensorRunner;
import org.liara.api.recognition.sensor.configuration.TVSensorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@UseSensorConfigurationOfType(TVSensorConfiguration.class)
@EmitStateOfType(ActivationState.class)
@Component
@Scope("prototype")
public class TVSensor extends AbstractVirtualSensorHandler
{
  @NonNull
  private final StateCollection _states;
  
  @NonNull
  private final SensorCollection _sensors;
  
  @Autowired
  public TVSensor (
    @NonNull final SensorCollection sensors,
    @NonNull final StateCollection states
  ) { 
    super();
    _sensors = sensors;
    _states = states; 
  }

  @Override
  @Transactional
  public void initialize (@NonNull final VirtualSensorRunner runner) {
    super.initialize(runner);
    
    final TVSensorConfiguration configuration = runner.getSensor()
                                                      .getConfiguration()
                                                      .as(TVSensorConfiguration.class);
    
    final Sensor sourceSensor = _sensors.findByIdentifier(configuration.getSourceSensorIdentifier()).get();
    
    final StateCollection sourceStates = _states.of(sourceSensor).apply(
      Operators.orderAscendingBy(entity -> entity.get("_emittionDate"))
    );
    
    DoubleState previous = null;
    ActivationState emitting = null;
    
    final Iterator<State> states = sourceStates.get().iterator();
    
    while (states.hasNext()) {
      final DoubleState next = (DoubleState) states.next();
      
      if (previous == null && next.getValue() > 0.0) {
        emitting = new ActivationState();
        emitting.setStart(next.getEmittionDate());
        emitting.setNode(runner.getSensor().getNode());
        emitting.setSensor(runner.getSensor());
        emitting.setEmittionDate(next.getEmittionDate());
        _states.getManager().persist(emitting);
      } else if (previous != null) {
        if (emitting != null && previous.getValue() > 0.0 && next.getValue() <= 0.0) {
          emitting.setEnd(next.getEmittionDate());
          emitting.setEmittionDate(next.getEmittionDate());
          _states.getManager().merge(emitting);
          emitting = null;
        } else if (emitting == null && previous.getValue() <= 0.0 && next.getValue() > 0.0) {
          emitting = new ActivationState();
          emitting.setStart(next.getEmittionDate());
          emitting.setNode(runner.getSensor().getNode());
          emitting.setSensor(runner.getSensor());
          emitting.setEmittionDate(next.getEmittionDate());
          _states.getManager().persist(emitting);
        }
      }
      
      previous = next;
    }
  }

  @Override
  public void sensorWillBeCreated (@NonNull final SensorWillBeCreatedEvent event) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void sensorWasCreated (@NonNull final SensorWasCreatedEvent event) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void nodeWillBeCreated (@NonNull final NodeWillBeCreatedEvent event) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void nodeWasCreated (@NonNull final NodeWasCreatedEvent event) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stateWillBeCreated (@NonNull final StateWillBeCreatedEvent event) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void stateWasCreated (@NonNull final StateWasCreatedEvent event) {
    // TODO Auto-generated method stub
    
  }
}
