package org.liara.api.resource.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.resource.ComputedCollectionResource;
import org.liara.api.resource.ModelResource;
import org.liara.api.resource.model.NodeModelBuilder;
import org.liara.api.utils.Builder;
import org.liara.api.utils.Duplicator;
import org.liara.collection.operator.Operator;

import java.util.Objects;

public class ChildrenNodeCollection
  extends ComputedCollectionResource<Node>
{
  @NonNull
  private final Node _target;

  @NonNull
  private final NodeModelBuilder _nodeModelBuilder;

  public ChildrenNodeCollection (
    @NonNull final ChildrenNodeCollectionBuilder builder
  ) {
    super(Node.class, Objects.requireNonNull(builder.getCollectionResourceBuilder()));
    _target = Duplicator.duplicate(Builder.require(builder.getTarget()));
    _nodeModelBuilder = Builder.require(builder.getNodeModelBuilder());
  }

  @Override
  public @NonNull Operator getOperator () {
    return Node.children();
  }

  public @NonNull Node getTarget () {
    return Duplicator.duplicate(_target);
  }

  @Override
  public @NonNull ModelResource<Node> toModelResource (@NonNull final Node node) {
    _nodeModelBuilder.setNode(node);
    return _nodeModelBuilder.build();
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ChildrenNodeCollection) {
      @NonNull
      final ChildrenNodeCollection otherChildrenNodeCollection = (ChildrenNodeCollection) other;

      return Objects.equals(_target, otherChildrenNodeCollection.getTarget());
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(super.hashCode(), _target);
  }
}
