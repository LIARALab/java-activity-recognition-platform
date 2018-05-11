package org.liara.api.data.entity.state.activity;

import org.liara.api.data.collection.ActivationStateCollection;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivityStateCreationSchema;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(BaseSleepingActivityState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class BaseSleepingActivityStateCreationSchema extends ActivityStateCreationSchema
{
  @Nullable
  private Long _relatedPresence;
  
  public void clear () {
    super.clear();
    
    _relatedPresence = null;
  }
  
  @Required
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

  @Override
  public BaseSleepingActivityState create () {
    return new BaseSleepingActivityState(this);
  }
}
