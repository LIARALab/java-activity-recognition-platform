package org.liara.api.resource.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.resource.collection.ChildrenNodeCollectionBuilder;
import org.liara.api.resource.collection.DeepChildrenNodeCollectionBuilder;
import org.liara.api.resource.collection.ParentNodeCollectionBuilder;
import org.liara.api.utils.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NodeModelBuilder
  implements Builder<NodeModel>
{
  @Nullable
  private Node _node;

  @Nullable
  private ChildrenNodeCollectionBuilder _childrenNodeCollectionBuilder;

  @Nullable
  private DeepChildrenNodeCollectionBuilder _deepChildrenNodeCollectionBuilder;

  @Nullable
  private ParentNodeCollectionBuilder _parentNodeCollectionBuilder;

  @Nullable
  private EntityManager _entityManager;

  public NodeModelBuilder () {
    _node = null;
    _childrenNodeCollectionBuilder = null;
    _deepChildrenNodeCollectionBuilder = null;
    _parentNodeCollectionBuilder = null;
    _entityManager = null;
  }

  public NodeModelBuilder (@NonNull final NodeModelBuilder toCopy) {
    _node = toCopy.getNode();
    _childrenNodeCollectionBuilder = toCopy.getChildrenNodeCollectionBuilder();
    _deepChildrenNodeCollectionBuilder = toCopy.getDeepChildrenNodeCollectionBuilder();
    _parentNodeCollectionBuilder = toCopy.getParentNodeCollectionBuilder();
    _entityManager = toCopy.getEntityManager();
  }

  @Override
  public @NonNull Class<NodeModel> getOutputClass () {
    return NodeModel.class;
  }

  @Override
  public @NonNull NodeModel build () {
    return new NodeModel(this);
  }

  public @Nullable EntityManager getEntityManager () {
    return _entityManager;
  }

  @Autowired
  public void setEntityManager (@Nullable final EntityManager entityManager) {
    _entityManager = entityManager;
  }

  public @Nullable Node getNode () {
    return _node;
  }

  public void setNode (@Nullable final Node node) {
    _node = node;
  }

  public @Nullable ChildrenNodeCollectionBuilder getChildrenNodeCollectionBuilder () {
    return _childrenNodeCollectionBuilder;
  }

  @Autowired
  public void setChildrenNodeCollectionBuilder (
    @Nullable final ChildrenNodeCollectionBuilder childrenNodeCollectionBuilder
  ) {
    _childrenNodeCollectionBuilder = childrenNodeCollectionBuilder;
  }

  public @Nullable ParentNodeCollectionBuilder getParentNodeCollectionBuilder () {
    return _parentNodeCollectionBuilder;
  }

  @Autowired
  public void setParentNodeCollectionBuilder (
    @Nullable final ParentNodeCollectionBuilder parentNodeCollectionBuilder
  ) {
    _parentNodeCollectionBuilder = parentNodeCollectionBuilder;
  }

  public @Nullable DeepChildrenNodeCollectionBuilder getDeepChildrenNodeCollectionBuilder () {
    return _deepChildrenNodeCollectionBuilder;
  }

  @Autowired
  public void setDeepChildrenNodeCollectionBuilder (
    @Nullable final DeepChildrenNodeCollectionBuilder deepChildrenNodeCollectionBuilder
  ) {
    _deepChildrenNodeCollectionBuilder = deepChildrenNodeCollectionBuilder;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof NodeModelBuilder) {
      @NonNull final NodeModelBuilder otherNodeModelBuilder = (NodeModelBuilder) other;

      return Objects.equals(_node, otherNodeModelBuilder.getNode()) &&
             Objects.equals(
               _childrenNodeCollectionBuilder,
               otherNodeModelBuilder.getChildrenNodeCollectionBuilder()
             ) &&
             Objects.equals(
               _deepChildrenNodeCollectionBuilder,
               otherNodeModelBuilder.getDeepChildrenNodeCollectionBuilder()
             ) &&
             Objects.equals(
               _parentNodeCollectionBuilder,
               otherNodeModelBuilder.getParentNodeCollectionBuilder()
             ) &&
             Objects.equals(_entityManager, otherNodeModelBuilder.getEntityManager());
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _node,
      _childrenNodeCollectionBuilder,
      _deepChildrenNodeCollectionBuilder,
      _parentNodeCollectionBuilder,
      _entityManager
    );
  }
}
