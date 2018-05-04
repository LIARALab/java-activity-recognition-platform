package org.liara.api.data.entity;

public interface EntitySnapshot<Entity> extends Cloneable
{
  public Entity getModel ();
  
  public EntitySnapshot<Entity> clone ();
}
