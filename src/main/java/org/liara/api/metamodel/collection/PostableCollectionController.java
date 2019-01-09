package org.liara.api.metamodel.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.validation.InvalidModelException;

public interface PostableCollectionController<Entity> extends CollectionController<Entity>
{
  @NonNull Long post (@NonNull final JsonNode json) throws JsonProcessingException, InvalidModelException;
}
