package org.liara.api.recognition.sensor.type;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public final class SensorTypeManagerFactory
{
  @Bean
  @Autowired
  public static SensorTypeManager createSensorTypeManager (
    @NonNull final ApplicationContext context
  )
  {
    @NonNull final SensorTypeManager               manager = new SensorTypeManager();
    @NonNull final Collection<@NonNull SensorType> types   = context.getBeansOfType(SensorType.class).values();

    for (@NonNull final SensorType type : types) {
      manager.register(type);
    }

    return manager;
  }
}
