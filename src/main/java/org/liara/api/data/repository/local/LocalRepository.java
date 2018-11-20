package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public abstract class LocalRepository
  implements ApplicationEntityManagerListener
{
  @Nullable
  private ApplicationEntityManager _parent;

  public LocalRepository () {
    _parent = null;
  }

  public LocalRepository (@NonNull final LocalRepository toCopy) {
    _parent = null;
    setParent(toCopy.getParent());
  }

  public @Nullable ApplicationEntityManager getParent () {
    return _parent;
  }

  public void setParent (@Nullable final ApplicationEntityManager parent) {
    if (_parent != parent) {
      if (_parent != null) {
        final ApplicationEntityManager oldParent = _parent;
        _parent = null;
        oldParent.removeListener(this);
      }

      _parent = parent;

      if (_parent != null) {
        _parent.addListener(this);
      }
    }
  }
}
