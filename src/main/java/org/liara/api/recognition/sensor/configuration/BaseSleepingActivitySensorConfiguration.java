package org.liara.api.recognition.sensor.configuration;

import org.liara.api.data.collection.SensorCollection;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class BaseSleepingActivitySensorConfiguration implements SensorConfiguration
{
  @Nullable
  private Long _sourcePresenceSensor = null;
  
  public BaseSleepingActivitySensorConfiguration () {
   
  }
  
  public BaseSleepingActivitySensorConfiguration (
    @NonNull final BaseSleepingActivitySensorConfiguration toCopy
  ) {
    _sourcePresenceSensor = toCopy.getSourcePresenceSensorIdentifier();
  }

  @IdentifierOfEntityInCollection(collection = SensorCollection.class)
  @Required
  public Long getSourcePresenceSensorIdentifier () {
    return _sourcePresenceSensor;
  }
  
  public void setSourcePresenceSensorIdentifier (@NonNull final Long sourcePresenceSensorIdentifier) {
    _sourcePresenceSensor = sourcePresenceSensorIdentifier;
  }

  @Override
  public BaseSleepingActivitySensorConfiguration clone () {
    return new BaseSleepingActivitySensorConfiguration(this);
  }
}
