package org.liara.api.data.entity.state.activity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.liara.api.data.collection.EntityCollections;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivationStateMutationSchema;
import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.data.schema.UseCreationSchema;
import org.liara.api.data.schema.UseMutationSchema;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "states_activity_base_sleeping_metadata")
@PrimaryKeyJoinColumn(name = "state_identifier")
@UseCreationSchema(BaseSleepingActivityStateCreationSchema.class)
@UseMutationSchema(ActivationStateMutationSchema.class)
public class BaseSleepingActivityState extends ActivityState
{
  @ManyToOne(optional = false)
  @JoinColumn(name = "related_presence_identifier", nullable = false, unique = false, updatable = true)
  private ActivationState _relatedPresence;
  
  public BaseSleepingActivityState () { }
  
  public BaseSleepingActivityState(
    @NonNull final BaseSleepingActivityStateCreationSchema schema
  ) {
    super(schema);
    
    _relatedPresence = EntityCollections.ACTIVATION_STATES.findByIdentifier(schema.getRelatedPresence()).get();
  }

  @JsonIgnore
  public ActivationState getRelatedPresence () {
    return _relatedPresence;
  }

  public void setRelatedPresence (@NonNull final ActivationState relatedPresence) {
    _relatedPresence = relatedPresence;
  }
  
  public Long getRelatedPresenceIdentifier () {
    return _relatedPresence.getIdentifier();
  }

  public void setRelatedPresenceIdentifier (@NonNull final Long identifier) {
    _relatedPresence = EntityCollections.ACTIVATION_STATES.findByIdentifier(identifier).get();
  }
}
