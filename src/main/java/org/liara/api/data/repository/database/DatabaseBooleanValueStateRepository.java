package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.BooleanValueState;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseBooleanValueStateRepository
  extends DatabaseValueStateRepository<Boolean, BooleanValueState>
  implements BooleanValueStateRepository
{
  @Autowired
  public DatabaseBooleanValueStateRepository (@NonNull final EntityManager entityManager) {
    super(entityManager, BooleanValueState.class);
  }
}
