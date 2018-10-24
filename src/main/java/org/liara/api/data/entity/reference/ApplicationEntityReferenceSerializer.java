package org.liara.api.data.entity.reference;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.lang.NonNull;

import java.io.IOException;

public class ApplicationEntityReferenceSerializer
  extends JsonSerializer<ApplicationEntityReference>
{
  @Override
  public void serialize (
    @NonNull final ApplicationEntityReference value,
    @NonNull final JsonGenerator gen,
    @NonNull final SerializerProvider serializers
  )
  throws IOException
  {
    if (value.isNull() || value.getIdentifier() == null) {
      gen.writeNull();
    } else {
      gen.writeNumber(value.getIdentifier());
    }
  }
}
