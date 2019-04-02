package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.LongValueState;
import org.liara.api.data.repository.LongValueStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseLongValueStateRepository
  extends DatabaseValueStateRepository<Long, LongValueState>
  implements LongValueStateRepository
{
  @Autowired
  public DatabaseLongValueStateRepository (@NonNull final EntityManager entityManager) {
    super(entityManager, LongValueState.class);
  }
}
