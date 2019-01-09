package org.liara.api.metamodel.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.validation.InvalidModelException;

import javax.persistence.EntityNotFoundException;

public interface PatchCollectionOperation<Entity>
  extends CollectionController<Entity>
{
  void patch (@NonNull final JsonNode json)
  throws EntityNotFoundException, JsonProcessingException, InvalidModelException;
}
