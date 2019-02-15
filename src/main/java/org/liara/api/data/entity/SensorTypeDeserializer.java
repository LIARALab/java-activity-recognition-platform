package org.liara.api.data.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.recognition.sensor.type.SensorTypeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class SensorTypeDeserializer
  extends StdDeserializer<SensorType>
{
  @NonNull
  private final SensorTypeManager _sensorTypeManager;

  @Autowired
  protected SensorTypeDeserializer (@NonNull final SensorTypeManager sensorTypeManager) {
    super(SensorType.class);

    _sensorTypeManager = sensorTypeManager;
  }

  @Override
  public @Nullable SensorType deserialize (
    @NonNull final JsonParser parser,
    @NonNull final DeserializationContext context
  )
  throws IOException {
    @NonNull final JsonToken token = parser.getCurrentToken();

    if (token == JsonToken.VALUE_NULL) {
      return null;
    } else if (token == JsonToken.VALUE_STRING) {
      @NonNull final String typeName = parser.getValueAsString();

      if (_sensorTypeManager.contains(typeName)) {
        return _sensorTypeManager.get(typeName);
      } else {
        throw new IOException(
          "Unknown sensor type name found at line " + parser.getCurrentLocation().getLineNr() +
          " and column " + parser.getCurrentLocation().getColumnNr() + " : " + typeName
        );
      }
    }

    throw new IOException(
      "Invalid json token found at line " + parser.getCurrentLocation().getLineNr() +
      " and column " + parser.getCurrentLocation().getColumnNr() + " : " +
      parser.getCurrentToken().toString() + ", only null and string tokens are convertible to " +
      "sensor type value."
    );
  }
}
