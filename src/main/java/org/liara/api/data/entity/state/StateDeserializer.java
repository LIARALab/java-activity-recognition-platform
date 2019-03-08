package org.liara.api.data.entity.state;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.repository.SensorRepository;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@JsonComponent
public class StateDeserializer
  extends JsonDeserializer<State>
{
  @Nullable
  private SensorRepository _sensorRepository;

  @Autowired
  public StateDeserializer (@NonNull final SensorRepository sensorRepository) {
    super();
    _sensorRepository = sensorRepository;
  }

  @Override
  public @Nullable State deserialize (
    @NonNull final JsonParser parser,
    @NonNull final DeserializationContext context
  )
  throws IOException {
    if (parser.currentToken() == JsonToken.VALUE_NULL) {
      return null;
    } else if (parser.currentToken() == JsonToken.START_OBJECT) {
      return parseState(parser);
    } else {
      throw new JsonParseException(
        parser,
        "Unable to parse " + State.class.getName() + " because the current json token " +
        parser.currentToken().toString() + " is nor " + JsonToken.VALUE_NULL + " nor " +
        JsonToken.START_OBJECT + "."
      );
    }
  }

  private @NonNull State parseState (
    @NonNull final JsonParser parser
  )
  throws IOException {
    @NonNull final JsonLocation location   = parser.getCurrentLocation();
    @NonNull final TreeNode     objectNode = parser.readValueAsTree();
    @NonNull final Optional<Sensor> sensor = _sensorRepository.find(
      parseSensorIdentifier(parser, location, objectNode)
    );

    if (sensor.isPresent()) {
      @NonNull final Class<? extends State> stateClass =
        Objects.requireNonNull(sensor.get().getTypeInstance()).getEmittedStateClass();

      @NonNull final JsonParser finalParser = objectNode.traverse();
      finalParser.setCodec(parser.getCodec());

      if (stateClass.equals(State.class)) {
        return parseRawState(finalParser);
      } else {
        return finalParser.readValueAs(stateClass);
      }
    } else {
      throw new JsonParseException(
        parser,
        "Unable to parse " + State.class.getName() + " because the given parent sensor identifier" +
        " does not refer to any existing sensor into the database.",
        location
      );
    }
  }

  private @NonNull State parseRawState (
    @NonNull final JsonParser parser
  )
  throws IOException {
    @NonNull final State       result  = new State();
    @NonNull final BeanWrapper wrapper = new BeanWrapperImpl(result);

    do {
      @NonNull final JsonToken token = parser.nextToken();

      if (token == JsonToken.FIELD_NAME) {
        @NonNull final String fieldName = parser.currentName();

        parser.nextValue();

        for (@NonNull final PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
          if (fieldName.equalsIgnoreCase(descriptor.getName())) {
            wrapper.setPropertyValue(
              descriptor.getName(),
              parser.readValueAs(descriptor.getPropertyType())
            );
          }
        }
      }
    } while (parser.currentToken() != JsonToken.END_OBJECT);

    return result;
  }

  private @NonNull Long parseSensorIdentifier (
    @NonNull final JsonParser parser,
    @NonNull final JsonLocation location,
    @NonNull final TreeNode objectNode
  )
  throws JsonParseException {
    @Nullable final TreeNode sensorIdentifierNode = objectNode.get("sensorIdentifier");

    if (sensorIdentifierNode == null) {
      throw new JsonParseException(
        parser,
        "Unable to parse " + State.class.getName() + " because no \"sensorIdentifier\" " +
        "field was found into the json object.",
        location
      );
    } else if (sensorIdentifierNode.isValueNode()) {
      @NonNull final ValueNode value = (ValueNode) sensorIdentifierNode;
      return value.asLong();
    } else {
      throw new JsonParseException(
        parser,
        "Unable to parse " + State.class.getName() + " because the \"sensorIdentifier\" field of " +
        "the given json object is not a value node."
      );
    }
  }
}

