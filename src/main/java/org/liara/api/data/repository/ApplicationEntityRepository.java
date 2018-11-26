package org.liara.api.data.repository;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ApplicationEntityRepository<Entity extends ApplicationEntity>
{
  Optional<Entity> find (
    @NonNull final ApplicationEntityReference<Entity> reference
  );

  Optional<Entity> find (
    @NonNull final Long identifier
  );

  default Entity getAt (@NonNull final ApplicationEntityReference<Entity> reference) {
    return find(reference).get();
  }

  List<Entity> findAll ();

  default List<Entity> getAt () {
    return findAll();
  }
}
