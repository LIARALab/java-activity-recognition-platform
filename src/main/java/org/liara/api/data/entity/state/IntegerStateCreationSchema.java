package org.liara.api.data.entity.state;

import java.util.Optional;

import org.liara.api.data.schema.Schema;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
  
  @JsonSetter
  public void setValue (@Nullable final Integer value) {
    _value = value;
  }
  
  public void setValue (@NonNull final Optional<Integer> value) {
    _value = value.orElse(null);
  }

  @Override
  public IntegerState create () {
    return new IntegerState(this);
  }
}
