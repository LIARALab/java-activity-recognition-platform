package org.liara.api.data.schema;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.entity.state.DoubleState;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.util.Optional;

@Schema(DoubleState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class DoubleStateMutationSchema extends StateMutationSchema
{
  @Nullable
  private Double _value = null;
  
  public Double getValue () {
    return _value;
  }
  
  @JsonSetter
  public void setValue (@Nullable final Double value) {
    _value = value;
  }
  
  public void setValue (@NonNull final Optional<Double> value) {
    _value = value.orElse(null);
  }

  public void apply (
    @NonNull final DoubleState state,
    @NonNull final EntityManager manager
  )
  {
    if (_value != null) state.setValue(_value);
  }
  
  @Override
  public DoubleState apply (@NonNull final EntityManager manager) {
    final DoubleState result = (DoubleState) getState().resolve(manager);

    apply(result, manager);
    super.apply(result, manager);
    
    return result;
  }
}
