package org.liara.api.metamodel.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.validation.InvalidModelException;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface PatchModelOperation<Entity>
  extends ModelController<Entity>
{
  void patch (@NonNull final Long identifier, @NonNull final JsonNode json)
  throws EntityNotFoundException, JsonProcessingException, InvalidModelException;

  void patch (@NonNull final UUID identifier, @NonNull final JsonNode json)
  throws EntityNotFoundException, JsonProcessingException, InvalidModelException;
}
