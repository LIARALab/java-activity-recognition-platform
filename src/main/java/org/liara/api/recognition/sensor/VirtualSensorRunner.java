package org.liara.api.recognition.sensor;

import org.jboss.logging.Logger;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import java.util.ArrayList;

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
    if (!VirtualSensorHandler.isVirtual(sensor)) {
      throw new Error("Unnable to instanciate a runner for a non-virtual sensor.");
    }
    
    @SuppressWarnings("unchecked") /* Checked virtual sensor */ final Class<? extends VirtualSensorHandler> handlerType = sensor
                                                                                                                            .getType()
                                                                                                                            .getClass()
                                                                                                                            .asSubclass(
                                                                                                                              VirtualSensorHandler.class);
    final ApplicationContext applicationContext = manager.getApplicationContext();
    /* @TODO check handler unicity */
    final VirtualSensorHandler handler = applicationContext.getBean(
      applicationContext.getBeanNamesForType(handlerType)[0], handlerType
    );
    
    final VirtualSensorRunner result = new VirtualSensorRunner(manager, sensor, handler);
    manager.registerRunner(result);
    return result;
  }

  public static VirtualSensorRunner create (
    @NonNull final VirtualSensorManager manager,
    @NonNull final Sensor sensor
  ) {
    final VirtualSensorRunner result = instanciate(manager, sensor);
    result.initialize();
    return result;
  }
  
  public static VirtualSensorRunner unbound (
    @NonNull final Sensor sensor,
    @NonNull final VirtualSensorHandler handler
  ) {
    if (!VirtualSensorHandler.isVirtual(sensor)) {
      throw new Error("Unnable to instanciate a runner for a non-virtual sensor.");
    }
    
    return new VirtualSensorRunner(null, sensor, handler);
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
  
  public void initialize () {
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
  
  public void resume () {
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
