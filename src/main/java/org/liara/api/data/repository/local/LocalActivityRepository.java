package org.liara.api.data.repository.local;

import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.data.repository.ActivityRepository;

public class LocalActivityRepository
  extends LocalTimeSeriesRepository<ActivityState>
  implements ActivityRepository
{
  public LocalActivityRepository () {
    super(ActivityState.class);
  }
}
