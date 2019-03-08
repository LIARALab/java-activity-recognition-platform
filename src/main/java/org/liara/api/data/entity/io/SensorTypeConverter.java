package org.liara.api.data.entity.io;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.SensorType;
import org.liara.api.recognition.sensor.type.SensorTypeManagerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
@Component
public class SensorTypeConverter
  implements AttributeConverter<SensorType, String>
{
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

    if (SensorTypeManagerFactory.INSTANCE.getObject().contains(dbData)) {
      return SensorTypeManagerFactory.INSTANCE.getObject().get(dbData);
    } else {
      throw new Error("Unable to convert the unknown sensor type name " + dbData + " to a sensor type instance.");
    }
  }
}

