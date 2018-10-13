package org.liara.api.data.schema;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.state.DoubleState;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Schema(DoubleState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class DoubleStateCreationSchema extends StateCreationSchema
{
  @Nullable
  private Double _value = null;
  
  @Required
  public Double getValue () {
    return _value;
  }

  public void clear () {
    super.clear();
    _value = null;
  }
  
  @JsonSetter
  public void setValue (@Nullable final Double value) {
    _value = value;
  }
  
  public void setValue (@NonNull final Optional<Double> value) {
    _value = value.orElse(null);
  }
}
