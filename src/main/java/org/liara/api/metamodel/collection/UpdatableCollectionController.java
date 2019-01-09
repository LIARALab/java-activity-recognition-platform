package org.liara.api.metamodel.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.validation.InvalidModelException;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface UpdatableCollectionController<Entity> extends CollectionController<Entity>
{
  @NonNull Long update (@NonNull final Long identifier, @NonNull final JsonNode json)
  throws EntityNotFoundException, JsonProcessingException, InvalidModelException;

  @NonNull Long update (@NonNull final UUID identifier, @NonNull final JsonNode json)
  throws EntityNotFoundException, JsonProcessingException, InvalidModelException;
}
