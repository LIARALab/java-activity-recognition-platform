package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.request.APIRequest;
import org.liara.request.validator.error.InvalidAPIRequestException;

public interface DeleteCollectionOperation<Entity>
  extends CollectionController<Entity>
{
  /**
   * Delete all models of the given collection that match all predicates of a given api request.
   *
   * @param request An api request that describe a set of predicates to fulfill.
   *
   * @return The number of deleted models.
   *
   * @throws InvalidAPIRequestException If one or more predicates of the given request can't be processed.
   */
  @NonNull Long delete (@NonNull final APIRequest request)
  throws InvalidAPIRequestException;
}
