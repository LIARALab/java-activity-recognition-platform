package org.liara.api.data.entity.state;

import java.util.Optional;

import org.liara.api.data.collection.BooleanStateCollection;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(BooleanState.class)
@JsonDeserialize(using = JsonDeserializer.None.class)
public class BooleanStateMutationSchema extends StateMutationSchema
{
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
  
  protected void apply (@NonNull final BooleanState state) {
    if (_value != null) state.setValue(_value);
  }
  
  @Override
  @IdentifierOfEntityInCollection(collection = BooleanStateCollection.class)
  @Required
  public Long getIdentifier () {
    return super.getIdentifier();
  }
  
  @Override
  public BooleanState apply (@NonNull final StateCollection collection) {
    final BooleanState result = (BooleanState) collection.findByIdentifier(getIdentifier()).get();
    
    apply(result);
    super.apply(result);
    
    return result;
  }
}
