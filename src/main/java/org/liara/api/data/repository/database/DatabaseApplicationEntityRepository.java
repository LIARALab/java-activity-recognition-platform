package org.liara.api.data.repository.database;

import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.repository.ApplicationEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
@Primary
public class DatabaseApplicationEntityRepository<Entity extends ApplicationEntity>
       implements ApplicationEntityRepository<Entity>
{
  @NonNull private final EntityManager _entityManager;
  
  @NonNull private final Class<Entity> _type;
  
  @Autowired
  public DatabaseApplicationEntityRepository (
    @NonNull final EntityManager entityManager,
    @NonNull final Class<Entity> type
  ) {
    _entityManager = entityManager;
    _type = type;
  }
  
  @Override
  public Optional<Entity> find (
    @NonNull final ApplicationEntityReference<? extends Entity> reference
  ) {
    return Optional.ofNullable(
      _entityManager.find(_type, reference.getIdentifier())
    );
  }

  @Override
  public List<Entity> findAll () {
    return _entityManager.createQuery(
      String.join("", "SELECT entity FROM ", _type.getName(), " entity"),
      _type
    ).getResultList();
  } 
}
