package org.liara.api.test.builder.node;

import java.util.function.Function;

import org.springframework.lang.NonNull;

import groovy.lang.Closure;

public final class HouseNodeBuilder extends ComposedNodeBuilder<HouseNodeBuilder>
{
  public HouseNodeBuilder() {
    super("common/house");
  }
  
  public RoomNodeBuilder withRoom () {
    return withChild(NodeBuilder.createRoom());
  }
  
  public HouseNodeBuilder withRoom (
    @NonNull final Function<RoomNodeBuilder, Void> callback
  ) {
    return withChild(NodeBuilder.createRoom(), callback);
  }

  public HouseNodeBuilder withRoom (
    @NonNull final Closure<RoomNodeBuilder> closure
  ) {
    return withChild(NodeBuilder.createRoom(), closure);
  }
  
  @Override
  protected HouseNodeBuilder self () {
    return this;
  }
}
