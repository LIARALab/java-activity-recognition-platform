package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.StringValueState;
import org.liara.api.data.repository.StringValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalStringValueStateRepository
  extends LocalValueStateRepository<java.lang.String, StringValueState>
  implements StringValueStateRepository
{

  public LocalStringValueStateRepository () { super(StringValueState.class); }
}
