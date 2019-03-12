package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.FloatValueState;
import org.liara.api.data.repository.FloatValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalFloatValueStateRepository
  extends LocalValueStateRepository<java.lang.Float, FloatValueState>
  implements FloatValueStateRepository
{

  public LocalFloatValueStateRepository () { super(FloatValueState.class); }
}
