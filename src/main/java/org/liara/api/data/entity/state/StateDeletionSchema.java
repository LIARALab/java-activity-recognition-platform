package org.liara.api.data.entity.state;

import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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

  public void setIdentifier (@Nullable final Long identifier) {
    _identifier = identifier;
  }
  
  @IdentifierOfEntityInCollection(collection = StateCollection.class)
  @Required
  public Long getIdentifier () {
    return _identifier;
  }
}
