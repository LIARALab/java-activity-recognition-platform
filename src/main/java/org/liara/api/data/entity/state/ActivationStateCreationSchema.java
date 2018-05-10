package org.liara.api.data.entity.state;

import java.util.Optional;

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

@Schema(ActivationStateCreationSchema.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class ActivationStateCreationSchema extends StateCreationSchema
{
  @Nullable
  private Long _startState;
  
  @Nullable
  private Long _endState;
  
  @Nullable
  private Long _node;
  
  public void clear () {
    super.clear();
    
    _startState = null;
    _endState = null;
    _node = null;
  }
  
  @Required
  @IdentifierOfEntityInCollection(collection = StateCollection.class)
  public Long getStartState () {
    return _startState;
  }
  
  @JsonSetter
  public void setStartState (@Nullable final Long startState) {
    _startState = startState;
  }
  
  public void setStartState (@NonNull final Optional<Long> startState) {
    _startState = startState.orElse(null);
  }
  
  public void setStartState (@Nullable final State startState) {
    _startState = startState == null ? null : startState.getIdentifier();
  }
  
  /* @TODO add after start test */
  @IdentifierOfEntityInCollection(collection = StateCollection.class)
  public Long getEndState () {
    return _endState;
  }
  
  @JsonSetter
  public void setEndState (@Nullable final Long endState) {
    _endState = endState;
  }
  
  public void setEndState (@NonNull final Optional<Long> endState) {
    _endState = endState.orElse(null);
  }
  public void setEndState (@Nullable final State endState) {
    _endState = endState == null ? null : endState.getIdentifier();
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
