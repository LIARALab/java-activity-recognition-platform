package org.liara.api.metamodel.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.error.InvalidAPIRequestException;

public interface IndexableCollectionController<Model> extends CollectionController<Model>
{
  /**
   * Return a collection of models that match all predicates of a given api request.
   *
   * @param request An api request that describe a set of predicates to fulfill.
   *
   * @return A collection of models that fulfill all predicates described into the given api request.
   *
   * @throws InvalidAPIRequestException If one or more predicates of the given request can't be processed.
   */
  @NonNull JPAEntityCollection<Model> index (@NonNull final APIRequest request) throws InvalidAPIRequestException;
}
