package org.liara.api.test.builder.node;

public class FurnitureNodeBuilder extends ComposedNodeBuilder<FurnitureNodeBuilder>
{
  public FurnitureNodeBuilder() {
    super("common/furniture");
  }

  @Override
  protected FurnitureNodeBuilder self () {
    return this;
  }
}
