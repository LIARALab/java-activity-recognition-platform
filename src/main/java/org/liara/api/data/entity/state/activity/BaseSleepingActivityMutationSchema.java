package org.liara.api.data.entity.state.activity;

import org.liara.api.data.collection.ActivationStateCollection;
import org.liara.api.data.collection.BaseSleepingActivityStateCollection;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivityStateMutationSchema;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(BaseSleepingActivityState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class BaseSleepingActivityMutationSchema extends ActivityStateMutationSchema
{  
  @Nullable
  private Long _relatedPresence;
  
  public void clear () {
    super.clear();
    
    _relatedPresence = null;
  }

  @IdentifierOfEntityInCollection(collection = ActivationStateCollection.class)
  public Long getRelatedPresence () {
    return _relatedPresence;
  }
  
  @JsonSetter
  public void setRelatedPresence (@Nullable final Long relatedPresence) {
    _relatedPresence = relatedPresence;
  }
  
  public void setRelatedPresence (@Nullable final ActivationState relatedPresence) {
    if (relatedPresence == null) {
      _relatedPresence = null;
    } else {
      _relatedPresence = relatedPresence.getIdentifier();
    }
  }

  protected void apply (@NonNull final BaseSleepingActivityState state) {
    if (_relatedPresence != null) state.setRelatedPresenceIdentifier(_relatedPresence);
  }
  
  @Override
  @IdentifierOfEntityInCollection(collection = BaseSleepingActivityStateCollection.class)
  @Required
  public Long getIdentifier () {
    return super.getIdentifier();
  }
  
  @Override
  public BaseSleepingActivityState apply (@NonNull final StateCollection collection) {
    final BaseSleepingActivityState result = (BaseSleepingActivityState) collection.findByIdentifier(getIdentifier()).get();
    
    apply(result);
    super.apply(result);
    
    return result;
  }
}
