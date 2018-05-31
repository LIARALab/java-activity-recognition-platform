package org.liara.api.test.builder.node;

import java.util.function.Function;

import org.springframework.lang.NonNull;

public class RoomNodeBuilder extends ComposedNodeBuilder<RoomNodeBuilder>
{
  public RoomNodeBuilder() {
    super("common/room");
  }
  
  public FurnitureNodeBuilder withFurniture () {
    return withChild(NodeBuilder.createFurniture());
  }
  
  public RoomNodeBuilder withFurniture (
    @NonNull final Function<FurnitureNodeBuilder, Void> callback
  ) {
    return withChild(NodeBuilder.createFurniture(), callback);
  }

  @Override
  protected RoomNodeBuilder self () {
    return this;
  }
}
