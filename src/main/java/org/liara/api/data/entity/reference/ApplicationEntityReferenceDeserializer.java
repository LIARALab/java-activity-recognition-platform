package org.liara.api.data.entity.reference;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;

public class ApplicationEntityReferenceDeserializer
  extends JsonDeserializer<ApplicationEntityReference>
  implements ContextualDeserializer
{
  @Nullable
  private final JavaType _type;

  public ApplicationEntityReferenceDeserializer () {
    _type = null;
  }

  public ApplicationEntityReferenceDeserializer (@NonNull final JavaType type) {
    _type = type;
  }

  @Override
  public ApplicationEntityReference deserialize (
    @NonNull final JsonParser parser,
    @NonNull final DeserializationContext context
  )
  throws IOException, JsonProcessingException
  {
    if (parser.currentToken() == JsonToken.VALUE_NULL) {
      return new ApplicationEntityReference(_type.containedType(0)
                                                 .getRawClass(), null);
    } else {
      return new ApplicationEntityReference(_type.containedType(0)
                                                 .getRawClass(), parser.getValueAsLong());
    }
  }

  @Override
  public JsonDeserializer<?> createContextual (
    @NonNull final DeserializationContext context,
    @NonNull final BeanProperty property
  )
  throws JsonMappingException
  {
    JavaType type = context.getContextualType() != null ? context.getContextualType() : property.getMember()
                                                                                                .getType();
    return new ApplicationEntityReferenceDeserializer(type);
  }
}
