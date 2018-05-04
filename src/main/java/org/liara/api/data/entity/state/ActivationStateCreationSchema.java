package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(ActivationStateCreationSchema.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class ActivationStateCreationSchema extends StateCreationSchema
{
  @Nullable
  private ZonedDateTime _start;
  
  @Nullable
  private ZonedDateTime _end;
  
  @Nullable
  private Long _node;
  
  @Required
  public ZonedDateTime getStart () {
    return _start;
  }
  
  @JsonSetter
  public void setStart (@Nullable final ZonedDateTime start) {
    _start = start;
  }
  
  public void setStart (@NonNull final Optional<ZonedDateTime> start) {
    _start = start.orElse(null);
  }
  
  /* @TODO add after start test */
  public ZonedDateTime getEnd () {
    return _end;
  }
  
  @JsonSetter
  public void setEnd (@Nullable final ZonedDateTime end) {
    _end = end;
  }
  
  public void setEnd (@NonNull final Optional<ZonedDateTime> end) {
    _end = end.orElse(null);
  }
  
  @Required
  @IdentifierOfEntityInCollection(collection = NodeCollection.class)
  public Long getNode () {
    return _node;
  }
  
  @JsonSetter
  public void setNode (@Nullable final Long node) {
    _node = node;
  }
  
  public void setNode (@Nullable final Node node) {
    if (node == null) {
      _node = null;
    } else {
      _node = node.getIdentifier();
    }
  }
  
  public void setNode (@NonNull final Optional<Long> node) {
    _node = node.orElse(null);
  }

  @Override
  public ActivationState create () {
    return new ActivationState(this);
  }
}
