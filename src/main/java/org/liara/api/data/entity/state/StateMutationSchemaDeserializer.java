package org.liara.api.data.entity.state;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.liara.api.data.collection.EntityCollections;
import org.liara.api.data.schema.UseMutationSchema;
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
public class StateMutationSchemaDeserializer extends JsonDeserializer<StateMutationSchema>
{
  @Autowired
  private ApplicationContext _context;
  
  @Override
  public StateMutationSchema deserialize (
    @NonNull final JsonParser parser, 
    @NonNull final DeserializationContext context
  ) throws IOException, JsonProcessingException {
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
        "Unnable to parse a StateMutationSchema from the provided json : ",
        "the given json is nor an object, nor a null value."
      ));
    }
  }

  private StateMutationSchema deserialize (
    @NonNull final ObjectNode node, 
    @NonNull final ObjectMapper mapper
  ) throws IOException {
    final Long stateIdentifier = (
      node.hasNonNull("identifier") ? node.get("identifier").asLong() 
                                    : null
    );
    
    if (stateIdentifier == null) {
      return deserializeDefault(node, mapper);
    } else {
      final Optional<State> state = (
        EntityCollections.STATES
                         .findByIdentifier(stateIdentifier)
      );
      
      if (state.isPresent()) {
        return deserializeForState(node, mapper, state.get());
      } else {
        return deserializeDefault(node, mapper);
      }
    }
  }

  @SuppressWarnings("unchecked") /* Checked by isAssignableFrom */
  private StateMutationSchema deserializeForState (
    @NonNull final ObjectNode node, 
    @NonNull final ObjectMapper mapper, 
    @NonNull final State state
  ) throws IOException {
    final Class<? extends State> stateClass = state.getClass();
    final UseMutationSchema useMutationSchema = stateClass.getAnnotation(
      UseMutationSchema.class
    );
    
    if (useMutationSchema == null) {
      throw new Error(String.join(
        "",
        "Unnable to parse StateMutationSchema for the state ",
        state.toString(), " because the given sensor state class ",
        stateClass.getName(), " does not declare a ", 
        UseMutationSchema.class.toString(),
        "annotation."
      ));
    } else {
      final Class<?> mutationSchema = useMutationSchema.value();
      if (StateMutationSchema.class.isAssignableFrom(mutationSchema)) {
        return deserializeSchema(
          node, mapper, 
          (Class<? extends StateMutationSchema>) mutationSchema
        );
      } else {
        throw new Error(String.join(
          "",
          "Unnable to parse StateMutationSchema for the state ", state.toString(), 
          " because the given sensor state mutation schema class ",
          mutationSchema.toString(), " is not a child class of ", 
          StateMutationSchema.class.toString()
        ));
      }
    }
  }

  private StateMutationSchema deserializeSchema (
    @NonNull final ObjectNode node,
    @NonNull final ObjectMapper mapper, 
    @NonNull final Class<? extends StateMutationSchema> mutationSchema
  ) throws IOException {
    return mapper.treeToValue(node, mutationSchema);
  }

  private StateMutationSchema deserializeDefault (
    @NonNull final ObjectNode node, 
    @NonNull final ObjectMapper mapper
  ) {
    final StateMutationSchema schema = (StateMutationSchema) _context.getBean("stateMutationSchema");
    
    schema.setIdentifier(node.hasNonNull("identifier") ? node.get("identifier").asLong() : null);
    schema.setEmittionDate(
      node.hasNonNull("emittionDate") ? ZonedDateTime.parse(node.get("emittionDate").asText()) 
                                      : null
    );
    
    return schema;
  }
}
