package org.liara.api.data.entity.state;

import java.util.Optional;

import org.liara.api.data.collection.IntegerStateCollection;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
  
  protected void apply (@NonNull final IntegerState state) {
    if (_value != null) state.setValue(_value);
  }
  
  @Override
  @IdentifierOfEntityInCollection(collection = IntegerStateCollection.class)
  @Required
  public Long getIdentifier () {
    return super.getIdentifier();
  }
  
  @Override
  public IntegerState apply (@NonNull final StateCollection collection) {
    final IntegerState result = (IntegerState) collection.findByIdentifier(getIdentifier()).get();
    
    apply(result);
    super.apply(result);
    
    return result;
  }
}
