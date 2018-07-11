package org.liara.api.recognition.sensor.configuration;

import org.liara.api.data.collection.SensorCollection;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class TVSensorConfiguration implements SensorConfiguration
{
  @Nullable
  private Long _sourceSensorIdentifier = null;
  
  public TVSensorConfiguration () {
   
  }
  
  public TVSensorConfiguration (@NonNull final TVSensorConfiguration toCopy) {
    _sourceSensorIdentifier = toCopy.getSourceSensorIdentifier();
  }

  @ValidApplicationEntityReference(collection = SensorCollection.class)
  @Required
  public Long getSourceSensorIdentifier () {
    return _sourceSensorIdentifier;
  }
  
  public void setSourceSensorIdentifier (@NonNull final Long sourceSensorIdentifier) {
    _sourceSensorIdentifier = sourceSensorIdentifier;
  }

  @Override
  public TVSensorConfiguration clone () {
    return new TVSensorConfiguration(this);
  }
}
