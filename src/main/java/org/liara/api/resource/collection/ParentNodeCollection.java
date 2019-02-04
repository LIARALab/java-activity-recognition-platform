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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class ParentNodeCollection
  extends ComputedCollectionResource<Node>
{
  @NonNull
  private final Node _target;

  @NonNull
  private final NodeModelBuilder _nodeModelBuilder;

  @Autowired
  public ParentNodeCollection (
    @NonNull final ParentNodeCollectionBuilder builder
  ) {
    super(Node.class, Builder.require(builder.getCollectionResourceBuilder()));
    _target = Duplicator.duplicate(Builder.require(builder.getTarget()));
    _nodeModelBuilder = Builder.require(builder.getNodeModelBuilder());
  }

  @Override
  public @NonNull Operator getOperator () {
    return Node.parents();
  }

  @Override
  protected @NonNull ModelResource<Node> toModelResource (final @NonNull Node entity) {
    _nodeModelBuilder.setNode(entity);
    return _nodeModelBuilder.build();
  }

  public @NonNull Node getTarget () {
    return Duplicator.duplicate(_target);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof ParentNodeCollection) {
      @NonNull final ParentNodeCollection otherParentNodeCollection = (ParentNodeCollection) other;

      return Objects.equals(_target, otherParentNodeCollection.getTarget());
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(super.hashCode(), _target);
  }
}
