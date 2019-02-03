package org.liara.api.resource.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.resource.CollectionResourceBuilder;
import org.liara.api.resource.model.NodeModelBuilder;
import org.liara.api.utils.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChildrenNodeCollectionBuilder
  implements Builder<ChildrenNodeCollection>
{
  @Nullable
  private Node _target;

  @Nullable
  private CollectionResourceBuilder _collectionResourceBuilder;

  @Nullable
  private ApplicationContext _context;

  public ChildrenNodeCollectionBuilder () {
    _target = null;
    _collectionResourceBuilder = null;
    _context = null;
  }

  public ChildrenNodeCollectionBuilder (@NonNull final ChildrenNodeCollectionBuilder toCopy) {
    _target = toCopy.getTarget();
    _collectionResourceBuilder = toCopy.getCollectionResourceBuilder();
    _context = toCopy.getContext();
  }

  @Override
  public @NonNull Class<ChildrenNodeCollection> getOutputClass () {
    return ChildrenNodeCollection.class;
  }

  @Override
  public @NonNull ChildrenNodeCollection build () {
    return new ChildrenNodeCollection(this);
  }

  public @Nullable Node getTarget () {
    return _target;
  }

  public void setTarget (@NonNull final Node target) {
    _target = target;
  }

  public @Nullable CollectionResourceBuilder getCollectionResourceBuilder () {
    return _collectionResourceBuilder;
  }

  @Autowired
  public void setCollectionResourceBuilder (
    @NonNull final CollectionResourceBuilder collectionResourceBuilder
  ) {
    _collectionResourceBuilder = collectionResourceBuilder;
  }

  public @Nullable ApplicationContext getContext () {
    return _context;
  }

  @Autowired
  public void setContext (@NonNull final ApplicationContext context) {
    _context = context;
  }

  public @Nullable NodeModelBuilder getNodeModelBuilder () {
    return _context == null ? null
                            : _context.getBean(NodeModelBuilder.class)
      ;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ChildrenNodeCollectionBuilder) {
      @NonNull final ChildrenNodeCollectionBuilder otherChildrenNodeCollectionBuilder =
        (ChildrenNodeCollectionBuilder) other;

      return Objects.equals(_target, otherChildrenNodeCollectionBuilder.getTarget()) &&
             Objects.equals(
               _collectionResourceBuilder,
               otherChildrenNodeCollectionBuilder.getCollectionResourceBuilder()
             ) &&
             Objects.equals(_context, otherChildrenNodeCollectionBuilder.getContext());
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(_target, _collectionResourceBuilder, _context);
  }
}
