package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.StringValueState;
import org.liara.api.data.repository.StringValueStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope("prototype")
@Primary
public class DatabaseStringValueStateRepository
  extends DatabaseValueStateRepository<java.lang.String, StringValueState>
  implements StringValueStateRepository
{
  @Autowired
  public DatabaseStringValueStateRepository (@NonNull final EntityManager entityManager) {
    super(
      entityManager,
      StringValueState.class
    );
  }
}
