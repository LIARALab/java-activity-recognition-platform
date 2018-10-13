package org.liara.api.data.schema;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Streams;
import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.utils.Beans;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Schema(BooleanState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class BooleanStateMutationSchema extends StateMutationSchema
{
  public static BooleanStateMutationSchema create (@NonNull final Map<String, ?> values) {
    return Beans.instanciate(BooleanStateMutationSchema.class, values);
  }
  
  public static List<BooleanStateMutationSchema> create (@NonNull final Iterable<Map<String, ?>> values) {
    return Streams.stream(values).map(BooleanStateMutationSchema::create).collect(Collectors.toList());
  }
  
  @Nullable
  private Boolean _value = null;
  
  public Boolean getValue () {
    return _value;
  }
  
  @JsonSetter
  public void setValue (@Nullable final Boolean value) {
    _value = value;
  }
  
  public void setValue (@NonNull final Optional<Boolean> value) {
    _value = value.orElse(null);
  }

  public void apply (
    @NonNull final BooleanState state,
    @NonNull final EntityManager manager
  )
  {
    if (_value != null) state.setValue(_value);
  }
  
  @Override
  public BooleanState apply (@NonNull final EntityManager manager) {
    final BooleanState result = (BooleanState) getState().resolve(manager);

    apply(result, manager);
    super.apply(result, manager);
    
    return result;
  }
}
