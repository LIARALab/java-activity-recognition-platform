package org.liara.api.database;

import java.io.IOException;

import javax.persistence.AttributeConverter;

import org.liara.api.recognition.sensor.SensorConfiguration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class SensorConfigurationConverter implements AttributeConverter<SensorConfiguration, String>
{
  private final ObjectMapper _mapper = new ObjectMapper();
  
  @Override
  public String convertToDatabaseColumn (@Nullable final SensorConfiguration attribute) {
    try {
      final ObjectNode node = _mapper.valueToTree(attribute);
      node.set("$$sensorConfigurationClass", _mapper.valueToTree(attribute.getClass()));
      return _mapper.writeValueAsString(node);
    } catch (final JsonProcessingException exception) {
      throw new Error("Unprocessable sensor configuration.", exception);
    }
  }

  @Override
  public SensorConfiguration convertToEntityAttribute (@Nullable final String dbData) {
    if (dbData == null || dbData.trim().equals("")) {
      return null;
    } else {
      System.out.println(dbData);
      try {
        final TreeNode node = _mapper.readTree(dbData);
        if (node.isObject()) {
          return tryToParseJsonObject((ObjectNode) node);
        } else if (node.isValueNode() && ((ValueNode) node).isNull()) {
          return null;
        } else {
          throw new Error("Invalid json object type, only objects and null nodes allowed.");
        }
      } catch (final IOException exception) {
        throw new Error("Invalid database json.", exception);
      } catch (final ClassNotFoundException exception) {
        throw new Error("Serialized configuration class does not exists.", exception);
      }
    }
  }

  private SensorConfiguration tryToParseJsonObject (
    @NonNull final ObjectNode node
  ) throws ClassNotFoundException, JsonProcessingException {
    final String configurationType = node.remove("$$sensorConfigurationClass").asText();
    final Class<?> configurationClass = Class.forName(configurationType);
    
    if (SensorConfiguration.class.isAssignableFrom(configurationClass)) {
      @SuppressWarnings("unchecked")
      final Class<? extends SensorConfiguration> validConfigurationClass = (Class<? extends SensorConfiguration>) configurationClass;
      return _mapper.treeToValue(node, validConfigurationClass);
    } else {
      throw new Error(String.join(
        "", 
        "The given serialized class : ", configurationClass.toString(),
        " is not a valid ", SensorConfiguration.class.toString(), " based class."
      ));
    }
  }
}
