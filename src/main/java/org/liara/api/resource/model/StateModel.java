package org.liara.api.resource.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.State;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.resource.BaseModelResource;
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.request.RestRequest;
import org.liara.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class StateModel extends BaseModelResource<State> {
  @NonNull
  private final APIEventPublisher _apiEventPublisher;

  @NonNull
  private final TransactionTemplate _transactionTemplate;

  @Autowired
  public StateModel (@NonNull final StateModelBuilder builder) {
    super(Objects.requireNonNull(builder.getBaseModelResourceBuilder()));
    _apiEventPublisher = Objects.requireNonNull(builder.getApiEventPublisher());
    _transactionTemplate = Objects.requireNonNull(builder.getTransactionTemplate());
  }

  @Override
  public @NonNull Mono<RestResponse> delete (@NonNull final RestRequest request)
          throws UnsupportedOperationException, IllegalRestRequestException {
    _transactionTemplate.execute(status -> this.tryToDelete());

    return Mono.just(RestResponse.ofType(String.class).empty());
  }

  private boolean tryToDelete () {
    try {
      _apiEventPublisher.delete(getModel());
      return true;
    } catch (@NonNull final Throwable throwable) {
      throw new Error(
              "Unable to delete state " + getModel().getIdentifier() + ".",
              throwable
      );
    }
  }
}
