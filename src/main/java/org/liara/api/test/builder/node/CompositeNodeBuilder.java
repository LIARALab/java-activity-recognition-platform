package org.liara.api.test.builder.node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.liara.api.test.builder.Builder;
import org.liara.api.test.builder.IdentityBuilder;
import org.springframework.lang.NonNull;

import com.google.common.collect.Streams;

public abstract class CompositeNodeBuilder<
                        Self extends CompositeNodeBuilder<Self, Entity>,
                        Entity extends Node
                      > 
                extends BaseNodeBuilder<Self, Entity>
{
  @NonNull
  private final Set<Builder<?, ? extends Node>> _children = new HashSet<>();
  
  public <SubBuilder extends Builder<?, ? extends Node>> Self withChild (
    @NonNull final SubBuilder builder
  ) {
    _children.add(builder);
    return self();
  }
  
  public Self withChild (@NonNull final Node node) {
    return withChild(IdentityBuilder.of(node));
  }
  
  public Set<Builder<?, ? extends Node>> getChildren () {
    return Collections.unmodifiableSet(_children);
  }

  @Override
  public void apply (@NonNull final Entity entity) {
    super.apply(entity);
    
    for (final Builder<?, ? extends Node> child : _children) {
      entity.addChild(child.build());
    }
  }

  @Override
  public Entity buildFor (@NonNull final LocalEntityManager entityManager) {
    final Entity result = super.buildFor(entityManager);
    entityManager.addAll(result.children());
    Streams.stream(result.children()).forEach(x -> {
      entityManager.addAll(x.sensors());
      Streams.stream(x.sensors()).forEach(y -> entityManager.addAll(y.states()));
    });
    return result;
  }
}
