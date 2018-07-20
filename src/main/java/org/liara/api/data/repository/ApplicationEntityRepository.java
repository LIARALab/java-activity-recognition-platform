package org.liara.api.data.repository;

import java.util.List;
import java.util.Optional;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.springframework.lang.NonNull;

public interface ApplicationEntityRepository<Entity extends ApplicationEntity>
{
  public Optional<Entity> find (
    @NonNull final ApplicationEntityReference<Entity> reference
  );
  
  public default Entity getAt (@NonNull final ApplicationEntityReference<Entity> reference) {
    return find(reference).get();
  }
  
  public List<Entity> findAll ();
  
  public default List<Entity> getAt () {
    return findAll();
  }
}
