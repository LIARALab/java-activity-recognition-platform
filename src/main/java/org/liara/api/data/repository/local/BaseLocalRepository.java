package org.liara.api.data.repository.local;

import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class BaseLocalRepository implements LocalEntityManagerListener
{
  @Nullable
  private LocalEntityManager _parent;

  public BaseLocalRepository() {
    _parent = null;
  }
  
  protected void entityWasAdded (@NonNull final ApplicationEntity entity) {
    
  }

  protected void entityWasRemoved (@NonNull final ApplicationEntity entity) {
    
  }
  
  public LocalEntityManager getParent () {
    return _parent;
  }

  public void setParent (@Nullable final LocalEntityManager parent) {
    if (_parent != parent) {
      if (_parent != null) {
        final LocalEntityManager oldParent = _parent;
        _parent = null;
        oldParent.removeListener(this);
      }
      
      _parent = parent;
      
      if (_parent != null) {
        _parent.addListener(this);
      }
    }
  }

  @Override
  public void add (@NonNull final ApplicationEntity entity) {
    _parent.add(entity);
    entityWasAdded(entity);
  }

  @Override
  public void remove (@NonNull final ApplicationEntity entity) {
    _parent.remove(entity);
    entityWasRemoved(entity);
  }
}
