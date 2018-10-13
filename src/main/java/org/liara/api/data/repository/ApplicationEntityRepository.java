package org.liara.api.data.repository;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

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
