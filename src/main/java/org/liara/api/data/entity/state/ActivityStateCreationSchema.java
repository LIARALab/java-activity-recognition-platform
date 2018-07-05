package org.liara.api.data.entity.state;

import java.time.ZonedDateTime;

import javax.persistence.EntityManager;

import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(ActivityState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class ActivityStateCreationSchema extends StateCreationSchema
{
  @Nullable
  private ZonedDateTime _start;
  
  @Nullable
  private ZonedDateTime _end;
  
  @Nullable
  private Long _node;
  
  @Nullable
  private String _tag;
  
  public void clear () {
    super.clear();
    
    _start = null;
    _end = null;
    _node = null;
    _tag = null;
  }
  
  @Required
  public ZonedDateTime getStart () {
    return _start;
  }
  
  public void setStart (@Nullable final ZonedDateTime start) {
    _start = start;
  }
  
  @Required
  public ZonedDateTime getEnd () {
    return _end;
  }
  
  public void setEnd (@Nullable final ZonedDateTime end) {
    _end = end;
  }
  
  @Required
  public String getTag () {
    return _tag;
  }
  
  public void setTag (@Nullable final String tag) {
    _tag = tag;
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

  @Override
  public ActivityState create (
    @NonNull final EntityManager manager
  ) {
    return new ActivityState(manager, this);
  }
}
