package org.liara.api.data.entity.state;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.liara.api.data.schema.Schema;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
  
  @JsonSetter
  public void setValue (@Nullable final Boolean value) {
    _value = value;
  }
  
  public void setValue (@NonNull final Optional<Boolean> value) {
    _value = value.orElse(null);
  }

  @Override
  public BooleanState create (
    @NonNull final EntityManager manager
  ) {
    return new BooleanState(manager, this);
  }
}
