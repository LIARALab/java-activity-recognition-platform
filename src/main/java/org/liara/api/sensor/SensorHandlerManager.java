package org.liara.api.sensor;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

@Service
public class SensorHandlerManager implements Iterable<SensorHandler>
{
  @NonNull
  private final ApplicationContext _applicationContext;
  
  @NonNull
  private final BiMap<Sensor, SensorHandler> _handlers = HashBiMap.create();
  
  @Autowired
  public SensorHandlerManager (@NonNull final ApplicationContext applicationContext) {
    _applicationContext = applicationContext;
  }
  
  public void registerHandler (@NonNull final Sensor sensor, @NonNull final String name) {
    final SensorHandler handler = SensorHandler.class.cast(_applicationContext.getBean(name));
    handler.attach(this, sensor);
  }
  
  public void registerHandler (@NonNull final Sensor sensor, @NonNull final Class<? super SensorHandler> handlerClass) {
    final SensorHandler handler = SensorHandler.class.cast(_applicationContext.getBean(handlerClass));
    handler.attach(this, sensor);
  }
  
  public void registerHandler (@NonNull final SensorHandler handler) {
    final Sensor handledSensor = handler.getHandledSensor();
    if (_handlers.containsKey(handledSensor) && _handlers.get(handledSensor) != handler) {
      throw new InvalidParameterException(String.join(
        "", 
        "Unnable to register the given handler : trying to register two different handlers ", 
        "for the same sensor instance. Please, unregister the previous handler before registering ",
        "a new one for the same sensor."
      ));
    }
    
    if (!_handlers.containsKey(handledSensor)) {
      _handlers.put(handledSensor, handler);
      handler.attach(this, handledSensor);
    }
  }
  
  public void unregisterHandler (@NonNull final Sensor sensor) {
    if (_handlers.containsKey(sensor)) {
      _handlers.remove(sensor).detach();
    }
  }
  
  public void unregisterHandler (@NonNull final SensorHandler handler) {
    if (_handlers.containsValue(handler)) {
      _handlers.inverse().remove(handler);
      handler.detach();
    }
  }
  
  public Set<SensorHandler> getHandlers () {
    return Collections.unmodifiableSet(_handlers.values());
  }
  
  public Iterator<SensorHandler> handlers () {
    return _handlers.values().iterator();
  }

  @Override
  public Iterator<SensorHandler> iterator () {
    return _handlers.values().iterator();
  }
}
