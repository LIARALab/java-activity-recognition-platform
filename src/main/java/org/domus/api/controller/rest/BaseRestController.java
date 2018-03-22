package org.domus.api.controller.rest;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.domus.api.collection.EntityCollection;
import org.domus.api.collection.EntityCollectionView;
import org.domus.api.executor.Executor;
import org.domus.api.executor.EntityCollectionRestrictionExecutor;
import org.domus.api.executor.InvalidAPIRequestException;
import org.domus.api.executor.RequestError;
import org.domus.api.request.APIRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public class BaseRestController {
  public <T> ResponseEntity<Iterable<T>> indexCollection (
    @NonNull final EntityCollection<T> collection,
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException {
    final EntityCollectionRestrictionExecutor<T> restriction = new EntityCollectionRestrictionExecutor<>();
    final APIRequest apiRequest = APIRequest.from(request);

    this.assertIsValidRequest(restriction, apiRequest);

    restriction.setCollection(collection);
    restriction.execute(apiRequest);

    EntityCollectionView<T> view = restriction.getResult();

    if (view.getSize() == collection.getSize()) {
      return new ResponseEntity<>(view.getContent(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(view.getContent(), HttpStatus.PARTIAL_CONTENT);
    }
  }

  public void assertIsValidRequest(
    @NonNull final Executor executor,
    @NonNull final APIRequest request
  ) throws InvalidAPIRequestException {
    final List<RequestError> errors = executor.validate(request);

    if (errors.size() > 0) {
      throw new InvalidAPIRequestException(errors);
    }
  }
}
