package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.NumericValueState;
import org.liara.api.data.repository.NumericValueStateRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LocalNumericValueStateRepository
  extends LocalValueStateRepository<Number, NumericValueState>
  implements NumericValueStateRepository
{

  public LocalNumericValueStateRepository () { super(NumericValueState.class); }
}
