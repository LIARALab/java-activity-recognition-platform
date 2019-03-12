package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.ShortValueState;
import org.liara.api.data.repository.ShortValueStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope("prototype")
@Primary
public class DatabaseShortValueStateRepository
  extends DatabaseValueStateRepository<java.lang.Short, ShortValueState>
  implements ShortValueStateRepository
{
  @Autowired
  public DatabaseShortValueStateRepository (@NonNull final EntityManager entityManager) {
    super(
      entityManager,
      ShortValueState.class
    );
  }
}
