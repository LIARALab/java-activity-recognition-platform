package org.liara.api.resource.collection;

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
public class NodeCollectionBuilder
  implements Builder<NodeCollection>
{
  @Nullable
  private CollectionResourceBuilder _collectionResourceBuilder;

  @Nullable
  private ApplicationEventPublisher _applicationEventPublisher;

  @Nullable
  private Validator _validator;

  public NodeCollectionBuilder () {
    _collectionResourceBuilder = null;
    _applicationEventPublisher = null;
    _validator = null;
  }

  public NodeCollectionBuilder (@NonNull final NodeCollectionBuilder toCopy) {
    _collectionResourceBuilder = toCopy.getCollectionResourceBuilder();
    _applicationEventPublisher = toCopy.getApplicationEventPublisher();
    _validator = toCopy.getValidator();
  }

  @Override
  public @NonNull Class<NodeCollection> getOutputClass () {
    return NodeCollection.class;
  }

  @Override
  public @NonNull NodeCollection build () {
    return new NodeCollection(this);
  }

  public @Nullable ApplicationEventPublisher getApplicationEventPublisher () {
    return _applicationEventPublisher;
  }

  @Autowired
  public @NonNull NodeCollectionBuilder setApplicationEventPublisher (
    @Nullable final ApplicationEventPublisher applicationEventPublisher
  ) {
    _applicationEventPublisher = applicationEventPublisher;
    return this;
  }

  public @Nullable Validator getValidator () {
    return _validator;
  }

  @Autowired
  public @NonNull NodeCollectionBuilder setValidator (@Nullable final Validator validator) {
    _validator = validator;
    return this;
  }

  public @Nullable CollectionResourceBuilder getCollectionResourceBuilder () {
    return _collectionResourceBuilder;
  }

  @Autowired
  public @NonNull NodeCollectionBuilder setCollectionResourceBuilder (
    @Nullable final CollectionResourceBuilder collectionResourceBuilder
  ) {
    _collectionResourceBuilder = collectionResourceBuilder;
    return this;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof NodeCollectionBuilder) {
      @NonNull
      final NodeCollectionBuilder otherNodeCollectionBuilder = (NodeCollectionBuilder) other;

      return Objects.equals(
        _collectionResourceBuilder,
        otherNodeCollectionBuilder.getCollectionResourceBuilder()
      ) &&
             Objects.equals(
               _applicationEventPublisher,
               otherNodeCollectionBuilder.getApplicationEventPublisher()
             ) &&
             Objects.equals(_validator, otherNodeCollectionBuilder.getValidator());
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _collectionResourceBuilder,
      _applicationEventPublisher,
      _validator
    );
  }
}
