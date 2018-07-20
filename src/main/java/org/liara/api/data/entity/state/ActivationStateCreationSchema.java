package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(ActivationState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class ActivationStateCreationSchema extends StateCreationSchema
{
  @Nullable
  private ZonedDateTime _start;
  
  @Nullable
  private ZonedDateTime _end;
  
  @NonNull
  private ApplicationEntityReference<Node> _node = ApplicationEntityReference.empty(Node.class);
  
  public void clear () {
    super.clear();
    
    _start = null;
    _end = null;
    _node = ApplicationEntityReference.empty(Node.class);
  }
  
  @Required
  public ZonedDateTime getStart () {
    return _start;
  }
  
  public void setStart (@Nullable final ZonedDateTime start) {
    _start = start;
  }
  
  /* @TODO add after start test */
  public ZonedDateTime getEnd () {
    return _end;
  }
  
  public void setEnd (@Nullable final ZonedDateTime end) {
    _end = end;
  }
  
  @Required
  @ValidApplicationEntityReference
  public ApplicationEntityReference<Node> getNode () {
    return _node;
  }
  
  @JsonSetter
  public void setNode (@Nullable final Long node) {
    _node = ApplicationEntityReference.of(Node.class, node);
  }
  
  public void setNode (@Nullable final Node node) {
    _node = (node == null) ? ApplicationEntityReference.empty(Node.class)
                           : ApplicationEntityReference.of(node);
  }
}
