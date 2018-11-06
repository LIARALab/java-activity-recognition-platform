package org.liara.api.data.schema;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;

@Schema(ActivationState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class DurationStateMutationSchema
  extends StateMutationSchema
{
  @Nullable
  private ZonedDateTime _start = null;

  @Nullable
  private ZonedDateTime _end = null;

  @Override
  public void clear () {
    super.clear();

    _start = null;
    _end = null;
  }

  @Required
  public @Nullable
  ZonedDateTime getStart () {
    return _start;
  }

  public void setStart (@Nullable final ZonedDateTime start) {
    _start = start;
  }

  @Required
  public @Nullable
  ZonedDateTime getEnd () {
    return _end;
  }

  public void setEnd (@Nullable final ZonedDateTime end) {
    _end = end;
  }

  public void apply (
    @NonNull final LabelState state, @NonNull final EntityManager manager
  )
  {
    if (_start != null) state.setStart(_start);
    if (_end != null) state.setEnd(_end);
  }
}
