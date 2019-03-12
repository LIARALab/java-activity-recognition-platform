package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.BooleanValueState;
import org.liara.api.data.repository.BooleanValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalBooleanValueStateRepository
  extends LocalValueStateRepository<java.lang.Boolean, BooleanValueState>
  implements BooleanValueStateRepository
{

  public LocalBooleanValueStateRepository () { super(BooleanValueState.class); }
}
