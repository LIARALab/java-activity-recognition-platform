package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(State.class)
@JsonDeserialize(using = StateMutationSchemaDeserializer.class)
public class StateMutationSchema
{
  @Nullable
  private Long _identifier = null;
  
  @Nullable
  private ZonedDateTime _emittionDate = null;
  
  public void clear () {
    _identifier = null;
    _emittionDate = null;
  }
  
  @Required
  @IdentifierOfEntityInCollection(collection = StateCollection.class)
  public Long getIdentifier () {
    return _identifier;
  }
  
  public void setIdentifier (@Nullable final State state) {
    if (state == null) {
      _identifier = null;
    } else {
      _identifier = state.getIdentifier();
    }
  }
  
  @JsonSetter
  public void setIdentifier (@Nullable final Long identifier) {
    _identifier = identifier;
  }
  
  public void setIdentifier (@NonNull final Optional<Long> identifier) {
    _identifier = identifier.orElse(null);
  }
  
  public ZonedDateTime getEmittionDate () {
    return _emittionDate;
  }

  @JsonSetter
  public void setEmittionDate (@Nullable final ZonedDateTime emittionDate) {
    _emittionDate = emittionDate;
  }
  
  public void setEmittionDate (@NonNull final Optional<ZonedDateTime> emittionDate) {
    _emittionDate = emittionDate.orElse(null);
  }
  
  protected void apply (@NonNull final State state) {
    if (_emittionDate != null) state.setEmittionDate(_emittionDate);
  }
  
  public State apply (@NonNull final StateCollection collection) {
    final State state = collection.findByIdentifier(_identifier).get();
    apply(state);
    return state;
  }
}
