package org.liara.api.data.entity.state;

import java.util.Optional;

import org.liara.api.data.collection.DoubleStateCollection;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
  
  protected void apply (@NonNull final DoubleState state) {
    if (_value != null) state.setValue(_value);
  }
  
  @Override
  @IdentifierOfEntityInCollection(collection = DoubleStateCollection.class)
  @Required
  public Long getIdentifier () {
    return super.getIdentifier();
  }
  
  @Override
  public DoubleState apply (@NonNull final StateCollection collection) {
    final DoubleState result = (DoubleState) collection.findByIdentifier(getIdentifier()).get();
    
    apply(result);
    super.apply(result);
    
    return result;
  }
}