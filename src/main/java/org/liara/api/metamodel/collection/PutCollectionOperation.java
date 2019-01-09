package org.liara.api.metamodel.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.metamodel.PutResult;
import org.liara.api.validation.InvalidModelException;

public interface PutCollectionOperation<Entity>
  extends CollectionController<Entity>
{
  /**
   * Put an element into the collection by using a json document.
   *
   * @param json Json to use in order to perform the replacement.
   *
   * @return The result of the operation.
   *
   * @throws JsonProcessingException If the given json is not valid.
   * @throws InvalidModelException   If the given json does not match the operation expected structure.
   */
  @NonNull PutResult put (@NonNull final JsonNode json)
  throws JsonProcessingException, InvalidModelException;
}
