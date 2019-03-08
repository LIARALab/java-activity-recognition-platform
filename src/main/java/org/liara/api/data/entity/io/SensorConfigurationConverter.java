package org.liara.api.data.entity.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.SensorConfiguration;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter(autoApply = true)
@Component
public class SensorConfigurationConverter
  implements AttributeConverter<SensorConfiguration, String>
{
  @NonNull
  private final ObjectMapper _mapper = new ObjectMapper();

  @Override
  public @NonNull String convertToDatabaseColumn (
    @Nullable final SensorConfiguration attribute
  )
  {
    if (attribute == null) {
      return "null";
    }

    try {
      @NonNull final ObjectNode node = _mapper.valueToTree(attribute);
      node.set("$$sensorConfigurationClass", _mapper.valueToTree(attribute.getClass()));
      return _mapper.writeValueAsString(node);
    } catch (@NonNull final JsonProcessingException exception) {
      throw new Error("Inconvertible sensor configuration.", exception);
    }
  }

  @Override
  public @Nullable SensorConfiguration convertToEntityAttribute (@Nullable final String dbData) {
    if (dbData == null || dbData.trim().equals("")) {
      return null;
    }

    try {
      @NonNull final TreeNode node = _mapper.readTree(dbData);
      if (node.isObject()) {
        return tryToParseJsonObject((ObjectNode) node);
      } else if (node.isValueNode() && ((ValueNode) node).isNull()) {
        return null;
      } else {
        throw new Error("Invalid json object type, only objects and null nodes allowed.");
      }
    } catch (@NonNull final IOException exception) {
      throw new Error("Invalid database json.", exception);
    } catch (@NonNull final ClassNotFoundException exception) {
      throw new Error("Serialized configuration class does not exists.", exception);
    }
  }

  private @NonNull SensorConfiguration tryToParseJsonObject (
    @NonNull final ObjectNode node
  )
  throws ClassNotFoundException, JsonProcessingException
  {
    @NonNull final String   configurationType  = node.remove("$$sensorConfigurationClass").asText();
    @NonNull final Class<?> configurationClass = Class.forName(configurationType);

    if (SensorConfiguration.class.isAssignableFrom(configurationClass)) {
      final Class<? extends SensorConfiguration> validConfigurationClass = configurationClass.asSubclass(
        SensorConfiguration.class);

      return _mapper.treeToValue(node, validConfigurationClass);
    } else {
      throw new Error("The given serialized class : " + configurationClass.toString() + " is not a child class of " +
                      SensorConfiguration.class.toString() + ".");
    }
  }
}

