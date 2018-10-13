package org.liara.api.data.schema;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.validation.Required;
import org.springframework.lang.Nullable;

@Schema(ActivityState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class ActivityStateCreationSchema
  extends DurationStateCreationSchema
{
  @Nullable
  private String _tag;

  public void clear () {
    super.clear();

    _tag = null;
  }

  @Required
  public String getTag () {
    return _tag;
  }

  public void setTag (@Nullable final String tag) {
    _tag = tag;
  }
}
