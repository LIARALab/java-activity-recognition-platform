package org.liara.api.data.entity.state;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.liara.api.data.schema.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.util.Optional;

@Schema(IntegerState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class IntegerStateMutationSchema extends StateMutationSchema
{
  @Nullable
  private Integer _value = null;
  
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

  public void apply (
    @NonNull final IntegerState state,
    @NonNull final EntityManager manager
  )
  {
    if (_value != null) state.setValue(_value);
  }
  
  @Override
  public IntegerState apply (@NonNull final EntityManager manager) {
    final IntegerState result = (IntegerState) getState().resolve(manager);

    apply(result, manager);
    super.apply(result, manager);
    
    return result;
  }
}
