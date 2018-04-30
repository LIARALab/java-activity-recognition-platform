package org.liara.recognition.sensor.configuration;

import org.liara.api.data.collection.SensorCollection;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.liara.recognition.sensor.SensorConfiguration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class TVSensorConfiguration implements SensorConfiguration
{
  @Nullable
  private Long _sourceSensorIdentifier = null;

  @IdentifierOfEntityInCollection(collection = SensorCollection.class)
  @Required
  public Long getSourceSensorIdentifier () {
    return _sourceSensorIdentifier;
  }
  
  public void setSourceSensorIdentifier (@NonNull final Long sourceSensorIdentifier) {
    _sourceSensorIdentifier = sourceSensorIdentifier;
  }
}
