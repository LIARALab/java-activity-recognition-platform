package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.DoubleValueState;
import org.liara.api.data.repository.DoubleValueStateRepository;
import org.liara.api.io.WritingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseDoubleValueStateRepository
  extends DatabaseValueStateRepository<Double, DoubleValueState>
  implements DoubleValueStateRepository
{
  @Autowired
  public DatabaseDoubleValueStateRepository (@NonNull final WritingSession writingSession) {
    super(writingSession, DoubleValueState.class);
  }
}
