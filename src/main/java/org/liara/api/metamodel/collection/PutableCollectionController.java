package org.liara.api.metamodel.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.validation.InvalidModelException;

import java.util.UUID;

public interface PutableCollectionController<Entity> extends CollectionController<Entity>
{
  @NonNull Long put (@NonNull final Long identifier, @NonNull final JsonNode json)
  throws JsonProcessingException, InvalidModelException;

  @NonNull Long put (@NonNull final UUID identifier, @NonNull final JsonNode json)
  throws JsonProcessingException, InvalidModelException;
}
