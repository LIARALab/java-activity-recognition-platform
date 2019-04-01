package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.NumericValueState;
import org.liara.api.data.repository.NumericValueStateRepository;
import org.liara.api.io.WritingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseNumericValueStateRepository
  extends DatabaseValueStateRepository<Number, NumericValueState>
  implements NumericValueStateRepository
{
  @Autowired
  public DatabaseNumericValueStateRepository (@NonNull final WritingSession writingSession) {
    super(writingSession, NumericValueState.class);
  }
}
