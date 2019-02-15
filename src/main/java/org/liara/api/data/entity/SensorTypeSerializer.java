package org.liara.api.data.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class SensorTypeSerializer
  extends StdSerializer<SensorType>
{
  public SensorTypeSerializer () {
    super(SensorType.class);
  }

  @Override
  public void serialize (
    @NonNull final SensorType value,
    @NonNull final JsonGenerator generator,
    @NonNull final SerializerProvider provider
  )
  throws IOException {
    generator.writeString(value.getName());
  }
}
