package org.liara.api.data.entity.state;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

@Schema(State.class)
public class StateDeletionSchema
{
  @NonNull
  private ApplicationEntityReference<State> _state;
  
  public StateDeletionSchema () {
    _state = ApplicationEntityReference.empty(State.class);
  }
  
  public StateDeletionSchema(@NonNull final State state) {
    _state = ApplicationEntityReference.of(state);
  }

  @JsonSetter
  public void setState (@Nullable final Long identifier) {
    _state = ApplicationEntityReference.of(State.class, identifier);
  }

  public void setState (@Nullable final State state) {
    _state = (state == null) ? ApplicationEntityReference.empty(State.class) 
                                  : ApplicationEntityReference.of(state);
  }
  
  @ValidApplicationEntityReference
  @Required
  public ApplicationEntityReference<State> getState () {
    return _state;
  }

  public void clear () {
    _state = ApplicationEntityReference.empty(State.class);
  }
}
