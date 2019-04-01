package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.state.ByteValueState;
import org.liara.api.data.repository.ByteValueStateRepository;
import org.liara.api.io.WritingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseByteValueStateRepository
  extends DatabaseValueStateRepository<Byte, ByteValueState>
  implements ByteValueStateRepository
{
  @Autowired
  public DatabaseByteValueStateRepository (@NonNull final WritingSession writingSession) {
    super(writingSession, ByteValueState.class);
  }
}
