package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.DoubleValueState;
import org.liara.api.data.repository.DoubleValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalDoubleValueStateRepository
  extends LocalValueStateRepository<java.lang.Double, DoubleValueState>
  implements DoubleValueStateRepository
{

  public LocalDoubleValueStateRepository () { super(DoubleValueState.class); }
}
