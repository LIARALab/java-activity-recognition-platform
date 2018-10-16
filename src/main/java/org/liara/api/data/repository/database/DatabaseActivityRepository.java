package org.liara.api.data.repository.database;

import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.data.repository.ActivityRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope("prototype")
@Primary
public class DatabaseActivityRepository
  extends DatabaseTimeSeriesRepository<ActivityState>
  implements ActivityRepository
{
  @NonNull
  private final EntityManager _entityManager;

  public DatabaseActivityRepository (
    @NonNull final EntityManager entityManager
  )
  {
    super(entityManager, ActivityState.class);

    _entityManager = entityManager;
  }
}
