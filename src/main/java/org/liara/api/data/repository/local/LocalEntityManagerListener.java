package org.liara.api.data.repository.local;

import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.lang.NonNull;

public interface LocalEntityManagerListener
{
  public void add (@NonNull final ApplicationEntity entity);
  public void remove (@NonNull final ApplicationEntity entity);
  
  public LocalEntityManager getParent ();
  public void setParent (@NonNull final LocalEntityManager parent);
}
