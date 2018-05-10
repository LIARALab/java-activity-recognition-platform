package org.liara.api.data.entity.state;

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
  private Long _startState = null;
  
  @Nullable
  private Long _endState = null;
  
  @Nullable
  private Long _node = null;

  @Override
  public void clear () {
    super.clear();
    
    _startState = null;
    _endState = null;
    _node = null;
  }
  
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
    if (startState == null) {
      _startState = null;
    } else {
      _startState = startState.getIdentifier();
    }
  }
  
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
    if (endState == null) {
      _endState = null;
    } else {
      _endState = endState.getIdentifier();
    }
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
    if (_startState != null) state.setStartStateIdentifier(_startState);
    if (_endState != null) state.setEndStateIdentifier(_endState);
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
