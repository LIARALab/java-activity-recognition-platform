package org.liara.api.data.entity.io;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Objects;

@JsonComponent
public class SensorDeserializer
  extends JsonDeserializer<Sensor>
{
  @Nullable
  private Sensor _currentSensor;

  @Nullable
  private JsonParser _currentConfigurationParser;

  @Autowired
  public SensorDeserializer () {
    super();
    _currentSensor = null;
    _currentConfigurationParser = null;
  }

  @Override
  public @Nullable Sensor deserialize (
    @NonNull final JsonParser parser,
    @NonNull final DeserializationContext context
  )
  throws IOException {
    onParsingStart();

    if (parser.currentToken() == JsonToken.VALUE_NULL) {
      _currentSensor = null;
    } else if (parser.currentToken() == JsonToken.START_OBJECT) {
      parseSensorObject(parser);
      parseSensorConfiguration();
    } else {
      throw new JsonParseException(
        parser,
        "Unable to parse " + Sensor.class.getName() + " because the current json token " +
        parser.currentToken().toString() + " is nor " + JsonToken.VALUE_NULL + " nor " +
        JsonToken.START_OBJECT + "."
      );
    }

    @Nullable final Sensor result = _currentSensor;

    onParsingEnd();

    return result;
  }

  private void parseSensorConfiguration ()
  throws IOException {
    if (_currentSensor == null) return;

    if (_currentConfigurationParser == null) {
      _currentSensor.setConfiguration(null);
    } else {
      _currentSensor.setConfiguration(
        _currentConfigurationParser.readValueAs(
          Objects.requireNonNull(_currentSensor.getTypeInstance()).getConfigurationClass()
        )
      );
    }
  }

  private void onParsingEnd () {
    _currentSensor = null;
    _currentConfigurationParser = null;
  }

  private void onParsingStart () {
    _currentSensor = new Sensor();
    _currentConfigurationParser = null;
  }

  private void parseSensorObject (
    @NonNull final JsonParser parser
  )
  throws IOException {
    do {
      @NonNull final JsonToken token = parser.nextToken();

      if (token == JsonToken.FIELD_NAME) {
        parseSensorObjectField(parser);
      }
    } while (parser.currentToken() != JsonToken.END_OBJECT);
  }

  private void parseSensorObjectField (
    @NonNull final JsonParser parser
  )
  throws IOException {
    @NonNull final String fieldName = parser.getCurrentName();
    @NonNull final BeanWrapper wrapper = new BeanWrapperImpl(
      Objects.requireNonNull(_currentSensor)
    );

    parser.nextValue();

    if (fieldName.equalsIgnoreCase("configuration")) {
      if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
        _currentConfigurationParser = null;
      } else {
        _currentConfigurationParser = parser.readValueAsTree().traverse();
        _currentConfigurationParser.setCodec(parser.getCodec());
      }
    } else {
      for (@NonNull final PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
        if (fieldName.equalsIgnoreCase(descriptor.getName())) {
          wrapper.setPropertyValue(
            descriptor.getName(),
            parser.readValueAs(descriptor.getPropertyType())
          );
        }
      }
    }
  }
}

