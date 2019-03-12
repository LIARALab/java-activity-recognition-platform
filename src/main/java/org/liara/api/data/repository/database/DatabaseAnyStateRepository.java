package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.repository.AnyStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope("prototype")
@Primary
public class DatabaseAnyStateRepository
  extends DatabaseStateRepository<org.liara.api.data.entity.state.State>
  implements AnyStateRepository
{
  @Autowired
  public DatabaseAnyStateRepository (final @NonNull EntityManager entityManager) {
    super(entityManager, org.liara.api.data.entity.state.State.class);
  }
}
