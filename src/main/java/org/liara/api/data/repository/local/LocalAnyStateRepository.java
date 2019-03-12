package org.liara.api.data.repository.local;

import org.liara.api.data.repository.AnyStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalAnyStateRepository
  extends LocalStateRepository<org.liara.api.data.entity.state.State>
  implements AnyStateRepository
{
  public LocalAnyStateRepository () {
    super(org.liara.api.data.entity.state.State.class);
  }
}
