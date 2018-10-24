package org.liara.api.data.schema;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.springframework.lang.Nullable;

@Schema(ActivationState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class ActivationStateCreationSchema
  extends DurationStateCreationSchema
{
  @Nullable
  private ApplicationEntityReference<? extends Node> _node = ApplicationEntityReference.empty(Node.class);

  public void clear () {
    super.clear();

    _node = ApplicationEntityReference.empty(Node.class);
  }

  @Required
  @ValidApplicationEntityReference
  public @Nullable
  ApplicationEntityReference<? extends Node> getNode () {
    return _node;
  }

  public void setNode (@Nullable final ApplicationEntityReference<? extends Node> node) {
    _node = node;
  }
}
