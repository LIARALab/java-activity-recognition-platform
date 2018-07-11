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
public class ActivationStateMutationSchema extends StateMutationSchema
{
  @Nullable
  private ZonedDateTime _start = null;
  
  @Nullable
  private ZonedDateTime _end = null;
  
  @NonNull
  private ApplicationEntityReference<Node> _node = ApplicationEntityReference.empty(Node.class);

  @Override
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
  
  public void setStart (@NonNull final ZonedDateTime start) {
    _start = start;
  }
  
  public ZonedDateTime getEnd () {
    return _end;
  }
  
  public void setEnd (@NonNull final ZonedDateTime end) {
    _end = end;
  }
  
  @ValidApplicationEntityReference
  @Required
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
  
  protected void apply (@NonNull final ActivationState state) {
    if (_start != null) state.setStart(_start);
    if (_end != null) state.setEnd(_end);
    if (_node != null) state.setNode(_node.resolve());;
  }
  
  @Override
  public ActivationState apply () {
    final ActivationState result = (ActivationState) getState().resolve();
    
    apply(result);
    super.apply(result);
    
    return result;
  }
}
