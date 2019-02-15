package org.liara.api.recognition.sensor.type;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.SensorType;
import org.liara.api.logging.InstantiationMessageFactory;
import org.liara.api.logging.Loggable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SensorTypeManagerFactory
  implements FactoryBean<SensorTypeManager>, Loggable
{
  @Nullable
  public static SensorTypeManagerFactory INSTANCE = null;

  @NonNull
  private final ApplicationContext _applicationContext;

  @Nullable
  private SensorTypeManager _sensorTypeManager;

  @Autowired
  public SensorTypeManagerFactory (@NonNull final ApplicationContext applicationContext) {
    _applicationContext = applicationContext;
    _sensorTypeManager = null;

    SensorTypeManagerFactory.INSTANCE = this;
  }

  @Override
  public SensorTypeManager getObject () {
    if (_sensorTypeManager == null) createSensorTypeManager();
    return _sensorTypeManager;
  }

  public void createSensorTypeManager () {
    info(InstantiationMessageFactory.instantiating(SensorTypeManager.class));

    _sensorTypeManager = new SensorTypeManager();

    for (@NonNull final SensorType type : _applicationContext.getBeansOfType(SensorType.class)
                                            .values()) {
      info("sensorType.registering " + type.getName() + " " + type.toString());
      _sensorTypeManager.register(type);
    }

    info(InstantiationMessageFactory.instantiated(SensorTypeManager.class, _sensorTypeManager));
  }

  @Override
  public Class<?> getObjectType () {
    return SensorTypeManager.class;
  }
}
