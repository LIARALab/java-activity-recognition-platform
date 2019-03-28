package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.FloatValueState;
import org.liara.api.data.repository.FloatValueStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseFloatValueStateRepository
  extends DatabaseValueStateRepository<Float, FloatValueState>
  implements FloatValueStateRepository
{
  @Autowired
  public DatabaseFloatValueStateRepository (
    @Qualifier("generatorEntityManager") @NonNull final EntityManager entityManager
  ) {
    super(entityManager, FloatValueState.class);
  }
}
