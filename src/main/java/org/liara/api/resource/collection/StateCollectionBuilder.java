package org.liara.api.resource.collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.io.APIEventPublisher;
import org.liara.api.resource.CollectionResourceBuilder;
import org.liara.api.utils.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.Validator;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class StateCollectionBuilder
  implements Builder<StateCollection>
{
  @Nullable
  private CollectionResourceBuilder _collectionResourceBuilder;

  @Nullable
  private APIEventPublisher _apiEventPublisher;

  @Nullable
  private Validator _validator;

  @Nullable
  private ObjectMapper _objectMapper;

  @Nullable
  private TransactionTemplate _transactionTemplate;

  public StateCollectionBuilder () {
    _collectionResourceBuilder = null;
    _apiEventPublisher = null;
    _validator = null;
    _objectMapper = null;
    _transactionTemplate = null;
  }

  public StateCollectionBuilder (@NonNull final StateCollectionBuilder toCopy) {
    _collectionResourceBuilder = toCopy.getCollectionResourceBuilder();
    _apiEventPublisher = toCopy.getAPIEventPublisher();
    _validator = toCopy.getValidator();
    _objectMapper = toCopy.getObjectMapper();
    _transactionTemplate = toCopy.getTransactionTemplate();
  }

  @Override
  public @NonNull Class<StateCollection> getOutputClass () {
    return StateCollection.class;
  }

  @Override
  public @NonNull StateCollection build () {
    return new StateCollection(this);
  }

  public @Nullable ObjectMapper getObjectMapper () {
    return _objectMapper;
  }

  @Autowired
  public void setObjectMapper (@Nullable final ObjectMapper objectMapper) {
    _objectMapper = objectMapper;
  }

  public @Nullable APIEventPublisher getAPIEventPublisher () {
    return _apiEventPublisher;
  }

  @Autowired
  public @NonNull StateCollectionBuilder setAPIEventPublisher (
    @Nullable final APIEventPublisher apiEventPublisher
  ) {
    _apiEventPublisher = apiEventPublisher;
    return this;
  }

  public @Nullable Validator getValidator () {
    return _validator;
  }

  @Autowired
  public @NonNull StateCollectionBuilder setValidator (@Nullable final Validator validator) {
    _validator = validator;
    return this;
  }

  public @Nullable CollectionResourceBuilder getCollectionResourceBuilder () {
    return _collectionResourceBuilder;
  }

  @Autowired
  public @NonNull StateCollectionBuilder setCollectionResourceBuilder (
    @Nullable final CollectionResourceBuilder collectionResourceBuilder
  ) {
    _collectionResourceBuilder = collectionResourceBuilder;
    return this;
  }

  public @Nullable TransactionTemplate getTransactionTemplate () {
    return _transactionTemplate;
  }

  @Autowired
  public void setTransactionTemplate (@Nullable final TransactionTemplate transactionTemplate) {
    _transactionTemplate = transactionTemplate;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof StateCollectionBuilder) {
      @NonNull
      final StateCollectionBuilder otherStateCollectionBuilder = (StateCollectionBuilder) other;

      return Objects.equals(
        _collectionResourceBuilder,
        otherStateCollectionBuilder.getCollectionResourceBuilder()
      ) && Objects.equals(
        _apiEventPublisher,
        otherStateCollectionBuilder.getAPIEventPublisher()
      ) && Objects.equals(
        _validator,
        otherStateCollectionBuilder.getValidator()
      ) && Objects.equals(
        _objectMapper,
        otherStateCollectionBuilder.getObjectMapper()
      ) && Objects.equals(
        _transactionTemplate,
        otherStateCollectionBuilder.getTransactionTemplate()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _collectionResourceBuilder,
      _apiEventPublisher,
      _validator,
      _objectMapper,
      _transactionTemplate
    );
  }
}
