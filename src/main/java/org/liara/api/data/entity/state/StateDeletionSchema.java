package org.liara.api.data.entity.state;

import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

@Schema(State.class)
public class StateDeletionSchema
{
  @Nullable
  private Long _identifier;
  
  public StateDeletionSchema () {
    _identifier = null;
  }
  
  public StateDeletionSchema(@NonNull final State state) {
    _identifier = state.getIdentifier();
  }

  @JsonSetter
  public void setIdentifier (@Nullable final Long identifier) {
    _identifier = identifier;
  }

  public void setIdentifier (@Nullable final BooleanState correlated) {
    _identifier = (correlated == null) ? null : correlated.getIdentifier();
  }
  
  @IdentifierOfEntityInCollection(collection = StateCollection.class)
  @Required
  public Long getIdentifier () {
    return _identifier;
  }

  public void clear () {
    _identifier = null;
  }
}
