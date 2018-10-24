package org.liara.api.data.schema;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;

@Schema(ActivityState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class ActivityStateMutationSchema extends StateMutationSchema
{
  @Nullable
  private ZonedDateTime _start;
  
  @Nullable
  private ZonedDateTime _end;
  
  @Nullable
  private String _tag;
  
  public void clear () {
    super.clear();
    
    _start = null;
    _end = null;
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

  public void apply (
    @NonNull final ActivityState state,
    @NonNull final EntityManager manager
  )
  {
    if (_start != null) state.setStart(_start);
    if (_end != null) state.setEnd(_end);
    if (_tag != null) state.setTag(_tag);
  }
  
  @Override
  public ActivityState apply (@NonNull final EntityManager manager) {
    final ActivityState result = (ActivityState) getState().resolve(manager);

    apply(result, manager);
    super.apply(result, manager);
    
    return result;
  }
}
