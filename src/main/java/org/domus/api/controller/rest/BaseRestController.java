package org.domus.api.controller.rest;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.domus.api.collection.EntityCollection;
import org.domus.api.collection.EntityCollectionView;
import org.domus.api.collection.EntityCollections;
import org.domus.api.executor.Executor;
import org.domus.api.executor.EntityCollectionRestrictionExecutor;
import org.domus.api.executor.InvalidAPIRequestException;
import org.domus.api.executor.RequestError;
import org.domus.api.executor.specification.SpecificationBuilder;
import org.domus.api.request.APIRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public class BaseRestController
{
  @Autowired
  @NonNull
  private EntityCollections _collections;

  public <T> ResponseEntity<Iterable<T>> indexCollection (
    @NonNull final Class<T> entity,
    @NonNull final HttpServletRequest request
  )
    throws InvalidAPIRequestException
  {
    final EntityCollectionRestrictionExecutor<T> restriction = new EntityCollectionRestrictionExecutor<>();
    final APIRequest apiRequest = APIRequest.from(request);
    final EntityCollection<T> collection = _collections.create(entity);

    this.assertIsValidRequest(apiRequest, restriction);

    restriction.setCollection(collection);
    restriction.execute(apiRequest);

    EntityCollectionView<T> view = restriction.getResult();

    if (view.getSize() == collection.getSize()) {
      return new ResponseEntity<>(view.getContent(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(view.getContent(), HttpStatus.PARTIAL_CONTENT);
    }
  }

  public <T> ResponseEntity<Iterable<T>> indexCollection (
    @NonNull final Class<T> entity,
    @NonNull final SpecificationBuilder<T> builder,
    @NonNull final HttpServletRequest request
  )
    throws InvalidAPIRequestException
  {
    final EntityCollectionRestrictionExecutor<T> restriction = new EntityCollectionRestrictionExecutor<>();
    final APIRequest apiRequest = APIRequest.from(request);

    this.assertIsValidRequest(apiRequest, restriction, builder);

    builder.execute(apiRequest);
    final EntityCollection<T> collection = _collections.create(entity, builder.getResult());

    restriction.setCollection(collection);
    restriction.execute(apiRequest);

    EntityCollectionView<T> view = restriction.getResult();

    if (view.getSize() == collection.getSize()) {
      return new ResponseEntity<>(view.getContent(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(view.getContent(), HttpStatus.PARTIAL_CONTENT);
    }
  }

  public <T> int countCollection (@NonNull final Class<T> entity, @NonNull final HttpServletRequest request)
    throws InvalidAPIRequestException
  {
    final EntityCollection<T> collection = _collections.create(entity);
    return collection.getSize();
  }

  public <T> int countCollection (
    @NonNull final Class<T> entity,
    @NonNull final SpecificationBuilder<T> builder,
    @NonNull final HttpServletRequest request
  )
    throws InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);

    this.assertIsValidRequest(apiRequest, builder);

    builder.execute(apiRequest);
    final EntityCollection<T> collection = _collections.create(entity, builder.getResult());

    return collection.getSize();
  }

  public void assertIsValidRequest (@NonNull final APIRequest request, @NonNull final Executor... executors)
    throws InvalidAPIRequestException
  {
    final List<RequestError> errors = new ArrayList<>();

    for (final Executor executor : executors) {
      errors.addAll(executor.validate(request));
    }

    if (errors.size() > 0) {
      throw new InvalidAPIRequestException(errors);
    }
  }
}
