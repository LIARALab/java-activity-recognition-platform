package org.liara.api.metamodel.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.PutResult;
import org.liara.api.metamodel.collection.CollectionController;
import org.liara.api.validation.InvalidModelException;

import java.util.UUID;

public interface PutModelOperation<Entity>
  extends CollectionController<Entity>
{
  @NonNull PutResult put (
    @NonNull final Long identifier, @NonNull final JsonNode json
  )
  throws JsonProcessingException, InvalidModelException;

  @NonNull PutResult put (
    @NonNull final UUID identifier, @NonNull final JsonNode json
  )
  throws JsonProcessingException, InvalidModelException;
}
