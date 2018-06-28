package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.liara.api.data.collection.ActivationStateCollection;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
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
  
  @Nullable
  private Long _node = null;

  @Override
  public void clear () {
    super.clear();
    
    _start = null;
    _end = null;
    _node = null;
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
  
  @IdentifierOfEntityInCollection(collection = NodeCollection.class)
  public Long getNode () {
    return _node;
  }
  
  @JsonSetter
  public void setNode (@Nullable final Long node) {
    _node = node;
  }
  
  public void setNode (@NonNull final Optional<Long> node) {
    _node = node.orElse(null);
  }
  
  public void setNode (@Nullable final Node node) {
    if (node == null) {
      _node = null;
    } else {
      _node = node.getIdentifier();
    }
  }
  
  protected void apply (@NonNull final ActivationState state) {
    if (_start != null) state.setStart(_start);
    if (_end != null) state.setEnd(_end);
    if (_node != null) state.setNodeIdentifier(_node);
  }
  
  @Override
  @IdentifierOfEntityInCollection(collection = ActivationStateCollection.class)
  @Required
  public Long getIdentifier () {
    return super.getIdentifier();
  }
  
  @Override
  public ActivationState apply (@NonNull final StateCollection collection) {
    final ActivationState result = (ActivationState) collection.findByIdentifier(getIdentifier()).get();
    
    apply(result);
    super.apply(result);
    
    return result;
  }
}
