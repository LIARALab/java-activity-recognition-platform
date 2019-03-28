package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.DoubleValueState;
import org.liara.api.data.repository.DoubleValueStateRepository;
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
public class DatabaseDoubleValueStateRepository
  extends DatabaseValueStateRepository<Double, DoubleValueState>
  implements DoubleValueStateRepository
{
  @Autowired
  public DatabaseDoubleValueStateRepository (
    @Qualifier("generatorEntityManager") @NonNull final EntityManager entityManager
  ) {
    super(entityManager, DoubleValueState.class);
  }
}
