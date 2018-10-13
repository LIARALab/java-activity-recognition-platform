package org.liara.api.data.schema;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.validation.Required;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;

@Schema(ActivityState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class DurationStateCreationSchema
  extends StateCreationSchema
{
  @Nullable
  private ZonedDateTime _start;
  
  @Nullable
  private ZonedDateTime _end;

  @Override
  public void clear () {
    super.clear();

    _start = null;
    _end = null;
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
}
