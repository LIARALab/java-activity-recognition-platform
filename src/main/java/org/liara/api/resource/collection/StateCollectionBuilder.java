package org.liara.api.resource.collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.resource.CollectionResourceBuilder;
import org.liara.api.utils.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
  private ApplicationEventPublisher _applicationEventPublisher;

  @Nullable
  private Validator _validator;

  @Nullable
  private ObjectMapper _objectMapper;

  public StateCollectionBuilder () {
    _collectionResourceBuilder = null;
    _applicationEventPublisher = null;
    _validator = null;
    _objectMapper = null;
  }

  public StateCollectionBuilder (@NonNull final StateCollectionBuilder toCopy) {
    _collectionResourceBuilder = toCopy.getCollectionResourceBuilder();
    _applicationEventPublisher = toCopy.getApplicationEventPublisher();
    _validator = toCopy.getValidator();
    _objectMapper = toCopy.getObjectMapper();
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

  public @Nullable ApplicationEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  @Autowired
  public @NonNull StateCollectionBuilder setApplicationEventPublisher (
    @Nullable final ApplicationEventPublisher applicationEventPublisher
  ) {
    _applicationEventPublisher = applicationEventPublisher;
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

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof StateCollectionBuilder) {
      @NonNull
      final StateCollectionBuilder otherNodeCollectionBuilder = (StateCollectionBuilder) other;

      return Objects.equals(
        _collectionResourceBuilder,
        otherNodeCollectionBuilder.getCollectionResourceBuilder()
      ) && Objects.equals(
        _applicationEventPublisher,
        otherNodeCollectionBuilder.getApplicationEventPublisher()
      ) && Objects.equals(
        _validator,
        otherNodeCollectionBuilder.getValidator()
      ) && Objects.equals(
        _objectMapper,
        otherNodeCollectionBuilder.getObjectMapper()
      );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _collectionResourceBuilder,
      _applicationEventPublisher,
      _validator,
      _objectMapper
    );
  }
}
