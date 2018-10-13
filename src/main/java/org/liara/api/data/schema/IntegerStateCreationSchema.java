package org.liara.api.data.schema;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.state.IntegerState;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Schema(IntegerState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class IntegerStateCreationSchema extends StateCreationSchema
{
  @Nullable
  private Integer _value = null;
  
  @Required
  public Integer getValue () {
    return _value;
  }

  public void clear () {
    super.clear();
    _value = null;
  }
  
  @JsonSetter
  public void setValue (@Nullable final Integer value) {
    _value = value;
  }
  
  public void setValue (@NonNull final Optional<Integer> value) {
    _value = value.orElse(null);
  }
}
