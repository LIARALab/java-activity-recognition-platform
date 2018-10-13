package org.liara.api.data.schema;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Schema(BooleanState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class BooleanStateCreationSchema extends StateCreationSchema
{
  @Nullable
  private Boolean _value = null;
  
  @Required
  public Boolean getValue () {
    return _value;
  }

  public void clear () {
    super.clear();
    _value = null;
  }
  
  @JsonSetter
  public void setValue (@Nullable final Boolean value) {
    _value = value;
  }
  
  public void setValue (@NonNull final Optional<Boolean> value) {
    _value = value.orElse(null);
  }
}
