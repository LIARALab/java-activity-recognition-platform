package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.IntegerValueState;
import org.liara.api.data.repository.IntegerValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalIntegerValueStateRepository
  extends LocalValueStateRepository<java.lang.Integer, IntegerValueState>
  implements IntegerValueStateRepository
{

  public LocalIntegerValueStateRepository () { super(IntegerValueState.class); }
}
