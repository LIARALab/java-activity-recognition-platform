package org.liara.api.data.entity;

import org.springframework.lang.NonNull;

public class BaseEntitySnapshot<Entity> implements EntitySnapshot<Entity>
{
  @NonNull
  private final Entity _model;
  
  public BaseEntitySnapshot (@NonNull final Entity model) {
    _model = model;
  }
  
  public BaseEntitySnapshot (@NonNull final BaseEntitySnapshot<Entity> toCopy) {
    _model = toCopy.getModel();
  }

  @Override
  public Entity getModel () {
    return _model;
  }

  @Override
  public EntitySnapshot<Entity> clone () {
    return new BaseEntitySnapshot<Entity>(this);
  }
}
