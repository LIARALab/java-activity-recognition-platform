package org.liara.api.metamodel.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.validation.InvalidModelException;

public interface PostCollectionOperation<Entity>
  extends CollectionController<Entity>
{
  /**
   * Add a new element into the collection by using some json content.
   *
   * @param json Json to use in order to perform the element addition.
   *
   * @return The added element's identifier.
   *
   * @throws JsonProcessingException If the given json is not in a valid format.
   * @throws InvalidModelException   If the given json does not match the operation expected structure.
   */
  @NonNull Long post (@NonNull final JsonNode json) throws JsonProcessingException, InvalidModelException;
}
