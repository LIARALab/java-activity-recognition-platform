package org.liara.api.data.schema;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.recognition.sensor.VirtualSensorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.ZonedDateTime;

@JsonComponent
public class StateCreationSchemaDeserializer extends JsonDeserializer<StateCreationSchema>
{
  @Autowired
  private ApplicationContext _context;

  @Autowired
  private EntityManager _entityManager;
  
  @Override
  public StateCreationSchema deserialize (
    @NonNull final JsonParser parser, 
    @NonNull final DeserializationContext context
  )
  throws IOException
  {
    final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
    final TreeNode treeNode = mapper.readTree(parser);
    
    if (treeNode.isObject()) {
      final ObjectNode node = (ObjectNode) treeNode;
      return deserialize(node, mapper);
    } else if (
      treeNode.isValueNode() && 
      ((ValueNode) treeNode).isNull()
    ) {
      return null;
    } else {
      throw new Error(String.join(
        "", 
        "Unnable to parse a StateCreationSchema from the provided json : ",
        "the given json is nor an object, nor a null value."
      ));
    }
  }

  private StateCreationSchema deserialize (
    @NonNull final ObjectNode node, 
    @NonNull final ObjectMapper mapper
  ) throws IOException {
    final Long sensorIdentifier = (
      node.hasNonNull("sensor") ? node.get("sensor").asLong() 
                                : null
    );
    
    if (sensorIdentifier == null) {
      return deserializeDefault(node, mapper);
    } else {
      @Nullable final Sensor sensor = _entityManager.find(Sensor.class, sensorIdentifier);

      return (sensor == null) ? deserializeDefault(node, mapper) : deserializeForSensor(node, mapper, sensor);
    }
  }

  @SuppressWarnings("unchecked") /* Checked by isAssignableFrom */
  private StateCreationSchema deserializeForSensor (
    @NonNull final ObjectNode node, 
    @NonNull final ObjectMapper mapper, 
    @NonNull final Sensor sensor
  ) throws IOException {
    final Class<? extends State> stateClass = VirtualSensorHandler.emittedStateOf(sensor);
    final UseCreationSchema useCreationSchema = stateClass.getAnnotation(
      UseCreationSchema.class
    );
    
    if (useCreationSchema == null) {
      throw new Error(String.join(
        "",
        "Unnable to parse StateCreationSchema for the sensor ",
        sensor.toString(), " because the given sensor state class ",
        stateClass.getName(), " does not declare a ", 
        UseCreationSchema.class.toString(),
        "annotation."
      ));
    } else {
      final Class<?> creationSchema = useCreationSchema.value();
      if (StateCreationSchema.class.isAssignableFrom(creationSchema)) {
        return deserializeSchema(
          node, mapper, 
          (Class<? extends StateCreationSchema>) creationSchema
        );
      } else {
        throw new Error(String.join(
          "",
          "Unnable to parse StateCreationSchema for the sensor ", sensor.toString(), 
          " because the given sensor state creation schema class ",
          creationSchema.toString(), " is not a child class of ", 
          StateCreationSchema.class.toString()
        ));
      }
    }
  }

  private StateCreationSchema deserializeSchema (
    @NonNull final ObjectNode node,
    @NonNull final ObjectMapper mapper, 
    @NonNull final Class<? extends StateCreationSchema> creationSchema
  ) throws IOException {
    return mapper.treeToValue(node, creationSchema);
  }

  private StateCreationSchema deserializeDefault (
    @NonNull final ObjectNode node, 
    @NonNull final ObjectMapper mapper
  ) {
    final StateCreationSchema schema = _context.getBean(StateCreationSchema.class);
    schema.setSensor(
      node.hasNonNull("sensor") ? ApplicationEntityReference.of(Sensor.class, node.get("sensor").asLong()) : null);
    schema.setEmittionDate(
      node.hasNonNull("emittionDate") ? ZonedDateTime.parse(node.get("sensor").asText()) 
                                      : null
    );
    
    return schema;
  }
}
