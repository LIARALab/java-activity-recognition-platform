package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.ByteValueState;
import org.liara.api.data.repository.ByteValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalByteValueStateRepository
  extends LocalValueStateRepository<java.lang.Byte, ByteValueState>
  implements ByteValueStateRepository
{

  public LocalByteValueStateRepository () { super(ByteValueState.class); }
}
