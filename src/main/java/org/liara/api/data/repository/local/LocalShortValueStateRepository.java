package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.ShortValueState;
import org.liara.api.data.repository.ShortValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalShortValueStateRepository
  extends LocalValueStateRepository<java.lang.Short, ShortValueState>
  implements ShortValueStateRepository
{

  public LocalShortValueStateRepository () { super(ShortValueState.class); }
}
