package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.recognition.sensor.type.SensorTypeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
@Component
public class SensorTypeConverter
  implements AttributeConverter<SensorType, String>
{
  @Nullable
  private static SensorTypeManager SENSOR_TYPE_MANAGER = null;

  @Autowired
  public static void setSensorTypeManager (@NonNull final SensorTypeManager sensorTypeManager) {
    SENSOR_TYPE_MANAGER = sensorTypeManager;
  }

  @Override
  public @Nullable String convertToDatabaseColumn (
    @Nullable final SensorType attribute
  )
  {
    return attribute == null ? null : attribute.getName();
  }

  @Override
  public @Nullable SensorType convertToEntityAttribute (@Nullable final String dbData) {
    if (dbData == null || dbData.trim().equals("")) {
      return null;
    }

    if (SENSOR_TYPE_MANAGER.contains(dbData)) {
      return SENSOR_TYPE_MANAGER.get(dbData);
    } else {
      throw new Error("Unable to convert the unknown sensor type name " + dbData + " to a sensor type instance.");
    }
  }
}

