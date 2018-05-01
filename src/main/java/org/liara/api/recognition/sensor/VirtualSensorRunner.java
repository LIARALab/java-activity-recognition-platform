package org.liara.api.recognition.sensor;

import java.util.ArrayList;

import org.jboss.logging.Logger;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

public class VirtualSensorRunner
{
  @NonNull
  private final Logger _logger = Logger.getLogger(VirtualSensorManager.class);
  
  @NonNull
  private final Sensor _sensor;
  
  @NonNull
  private final VirtualSensorHandler _handler;
  
  @NonNull
  private VirtualSensorRunnerState _state;
  
  @NonNull
  private final VirtualSensorManager _manager;
  
  @NonNull
  private final ArrayList<State> _buffer = new ArrayList<>(200);
  
  private static VirtualSensorRunner instanciate (
    @NonNull final VirtualSensorManager manager,
    @NonNull final Sensor sensor
  ) {
    if (!sensor.isVirtual()) {
      throw new Error("Unnable to instanciate a runner for a non-virtual sensor.");
    }
    
    try {
      @SuppressWarnings("unchecked") /* Checked virtual sensor */
      final Class<? extends VirtualSensorHandler> handlerType = (Class<? extends VirtualSensorHandler>) sensor.getTypeClass();
      final ApplicationContext applicationContext = manager.getApplicationContext();
      /* @TODO check handler unicity */
      final VirtualSensorHandler handler = applicationContext.getBean(
        applicationContext.getBeanNamesForType(handlerType)[0], handlerType
      );
      
      final VirtualSensorRunner result = new VirtualSensorRunner(manager, sensor, handler);
      manager.registerRunner(result);
      return result;
    } catch (final Exception exception) {
      throw new Error(String.join(
        "", 
        "Unnable to instanciate a virtual sensor handler for the virtual type ",
        sensor.getType(), ". Note that, a virtual sensor handler must always have an ",
        "empty constructor."
      ));
    }
  }
  
  public static VirtualSensorRunner create (
    @NonNull final VirtualSensorManager manager,
    @NonNull final Sensor sensor
  ) {
    final VirtualSensorRunner result = instanciate(manager, sensor);
    result.initialize();
    return result;
  }
  
  public static VirtualSensorRunner restart (
    @NonNull final VirtualSensorManager manager,
    @NonNull final Sensor sensor
  ) {
    final VirtualSensorRunner result = instanciate(manager, sensor);
    result.resume();
    return result;
  }
  
  
  protected VirtualSensorRunner (
    @NonNull final VirtualSensorManager manager,
    @NonNull final Sensor sensor,
    @NonNull final VirtualSensorHandler handler
  ) {
    _manager = manager;
    _sensor = sensor;
    _handler = handler;
    _state = VirtualSensorRunnerState.INSTANCIATED;
  }
  
  protected void initialize () {
    if (_state == VirtualSensorRunnerState.INSTANCIATED) {
      _state = VirtualSensorRunnerState.INITIALIZING;
      _handler.initialize(this);
      _state = VirtualSensorRunnerState.WAITING;
    } else {
      throw new Error(String.join(
        "", 
        "Unnable to initialize the current virtual sensor runner instance, only instanciated ",
        "virtual sensor runner instance can be initialized. The current state is : " + _state + "."
      ));
    }
  }
  
  protected void resume () {
    switch (_state) {
      case INSTANCIATED:
      case PAUSED:
        _state = VirtualSensorRunnerState.RESUMING;
        _handler.resume(this);
        _state = VirtualSensorRunnerState.WAITING;
        break;
      default:
        throw new Error(String.join(
          "", 
          "Unnable to resume the current virtual sensor runner instance, only instanciated and paused ",
          "virtual sensor runner instance can be resumed. The current state is : " + _state + "."
        ));
    }
  }
  
  public void pause () {
    if (_state == VirtualSensorRunnerState.WAITING) {
      _state = VirtualSensorRunnerState.PAUSING;
      _handler.pause();
      _state = VirtualSensorRunnerState.PAUSED;
    } else {
      throw new Error(String.join(
        "", 
        "Unnable to pause the current virtual sensor runner instance, only waiting ",
        "virtual sensor runner instance can be paused. The current state is : " + _state + "."
      ));
    }
  }
  
  public void stop () {
    if (_state == VirtualSensorRunnerState.WAITING) {
      _state = VirtualSensorRunnerState.STOPPING;
      _handler.stop();
      _state = VirtualSensorRunnerState.STOPPED;
    } else {
      throw new Error(String.join(
        "", 
        "Unnable to stop the current virtual sensor runner instance, only waiting ",
        "virtual sensor runner instance can be stopped. The current state is : " + _state + "."
      ));
    }
  }

  public Sensor getSensor () {
    return _sensor;
  }

  public VirtualSensorHandler getHandler () {
    return _handler;
  }
  
  public VirtualSensorManager getManager () {
    return _manager;
  }
  
  public VirtualSensorRunnerState getState () {
    return _state;
  }
}
