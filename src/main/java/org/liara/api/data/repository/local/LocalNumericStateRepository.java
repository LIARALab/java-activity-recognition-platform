package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.NumericState;
import org.liara.api.data.repository.NumericStateRepository;

public class LocalNumericStateRepository
  extends LocalTimeSeriesRepository<NumericState>
  implements NumericStateRepository
{
  public LocalNumericStateRepository () {
    super(NumericState.class);
  }
}
