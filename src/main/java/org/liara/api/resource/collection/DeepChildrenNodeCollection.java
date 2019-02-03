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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DeepChildrenNodeCollection
  extends ComputedCollectionResource<Node>
{
  @NonNull
  private final Node _target;

  @NonNull
  private final NodeModelBuilder _nodeModelBuilder;

  @Autowired
  public DeepChildrenNodeCollection (
    @NonNull final DeepChildrenNodeCollectionBuilder builder
  ) {
    super(Node.class, Builder.require(builder.getCollectionResourceBuilder()));
    _target = Duplicator.duplicate(Builder.require(builder.getTarget()));
    _nodeModelBuilder = Builder.require(builder.getNodeModelBuilder());
  }

  @Override
  public @NonNull Operator getOperator () {
    return _target.deepChildren();
  }

  public @NonNull Node getTarget () {
    return Duplicator.duplicate(_target);
  }


  @Override
  protected @NonNull ModelResource<Node> toModelResource (final @NonNull Node entity) {
    _nodeModelBuilder.setNode(entity);
    return _nodeModelBuilder.build();
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof DeepChildrenNodeCollection) {
      @NonNull final DeepChildrenNodeCollection otherDeepChildrenNodeCollection =
        (DeepChildrenNodeCollection) other;

      return Objects.equals(_target, otherDeepChildrenNodeCollection.getTarget());
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(super.hashCode(), _target);
  }
}
