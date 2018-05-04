package org.liara.api.data.entity.sensor;

import java.io.IOException;

import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.recognition.sensor.UseSensorConfigurationOfType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

@JsonComponent
public class SensorCreationSchemaDeserializer extends JsonDeserializer<SensorCreationSchema>
{ 
  @Autowired
  private ApplicationContext _context;
  
  @Override
  public SensorCreationSchema deserialize (
    @NonNull final JsonParser parser, 
    @NonNull final DeserializationContext context
  ) throws IOException, JsonProcessingException {
    final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
    
    final TreeNode treeNode = mapper.readTree(parser);
    
    if (treeNode.isObject()) {
      final ObjectNode node = (ObjectNode) treeNode;
      final TreeNode configuration;
      
      if (node.has("configuration")) {      
        configuration = node.get("configuration");
      } else {
        configuration = null;
      }
      
      final SensorCreationSchema result = deserializeSchema(node);
      
      result.setConfiguration(deserializeConfiguration(result, configuration, mapper));
      
      return result;
    } else if (treeNode.isValueNode() && ((ValueNode) treeNode).isNull()) {
      return null;
    } else {
      throw new Error(String.join(
        "", 
        "Unnable to parse a SensorCreationSchema from the provided json : ",
        "the provided json is nor an object nor a null value."
      ));
    }
  }

  private SensorCreationSchema deserializeSchema (@NonNull final ObjectNode node) {
    final SensorCreationSchema result = _context.getBean(SensorCreationSchema.class);
    result.setName((node.hasNonNull("name")) ? node.get("name").asText() : null);
    result.setType((node.hasNonNull("type")) ? node.get("type").asText() : null);
    result.setValueType((node.hasNonNull("valueType")) ? node.get("valueType").asText() : null);
    result.setValueUnit((node.hasNonNull("valueUnit")) ? node.get("valueUnit").asText() : null);
    result.setValueLabel((node.hasNonNull("valueLabel")) ? node.get("valueLabel").asText() : null);
    result.setIpv4Address((node.hasNonNull("ipv4Address")) ? node.get("ipv4Address").asText() : null);
    result.setIpv6Address((node.hasNonNull("ipv6Address")) ? node.get("ipv6Address").asText() : null);
    result.setParent((node.hasNonNull("parent")) ? node.get("parent").asLong() : null);
    return result;
  }

  private SensorConfiguration deserializeConfiguration (
    @NonNull final SensorCreationSchema result,
    @NonNull final TreeNode configuration,
    @NonNull final ObjectMapper mapper
  ) {
    try {
      final Class<?> sensorType = Class.forName(result.getType());
      final UseSensorConfigurationOfType annotation = sensorType.getAnnotation(UseSensorConfigurationOfType.class);
      
      if (annotation == null) {
        return null;
      } else if (configuration == null || (configuration.isValueNode() && ((ValueNode) configuration).isNull())) {
        return (annotation == null) ? null : annotation.value().newInstance(); 
      } else if (configuration.isObject()) {
        return (annotation == null) ? null 
                                    : mapper.treeToValue(configuration, annotation.value());
      } else {
        throw new Error("Invalid configuration node type.");
      }
    } catch (final Exception exception) {
      return null;
    }
  }
}
