package org.liara.api.test.builder.node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.SchemaManager;
import org.springframework.lang.NonNull;

import groovy.lang.Closure;

public abstract class ComposedNodeBuilder<Self extends ComposedNodeBuilder<Self>> extends NodeBuilder<Self>
{
  @NonNull
  private final List<NodeBuilder<?>> _children = new ArrayList<>();
  
  public ComposedNodeBuilder(@NonNull final String type) {
    super(type);
  }
  
  public <SubBuilder extends NodeBuilder<?>> SubBuilder withChild (@NonNull final SubBuilder builder) {
    _children.add(builder);
    return builder;
  }
  
  public <SubBuilder extends NodeBuilder<?>> Self withChild (
    @NonNull final SubBuilder builder,
    @NonNull final Function<SubBuilder, Void> callback
  ) {
    _children.add(builder);
    callback.apply(builder);
    return self();
  }
  
  public <SubBuilder extends NodeBuilder<?>> Self withChild (
    @NonNull final SubBuilder builder,
    @NonNull final Closure<SubBuilder> closure
  ) {    
    _children.add(builder);
    
    final Closure<SubBuilder> hydrated = closure.rehydrate(
      builder, closure.getOwner(), closure.getThisObject()
    );
    
    hydrated.setResolveStrategy(Closure.DELEGATE_ONLY);
    hydrated.call();
    
    return self();
  }

  @Override
  public Node build (@NonNull final SchemaManager manager) {
    final Node node = super.build(manager);
    
    for (final NodeBuilder<?> builder : _children) {
      builder.withParent(node);
      builder.build(manager);
    }
    
    return node;
  }
}
