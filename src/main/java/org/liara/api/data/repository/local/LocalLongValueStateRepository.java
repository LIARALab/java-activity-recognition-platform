package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.LongValueState;
import org.liara.api.data.repository.LongValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalLongValueStateRepository
  extends LocalValueStateRepository<java.lang.Long, LongValueState>
  implements LongValueStateRepository
{
  public LocalLongValueStateRepository () { super(LongValueState.class); }
}
