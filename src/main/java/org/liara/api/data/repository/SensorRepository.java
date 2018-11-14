package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Sensor;

import java.util.List;

public interface SensorRepository extends ApplicationEntityRepository<Sensor>
{
  @NonNull List<@NonNull Sensor> getSensorsOfType (@NonNull final String type);

  @NonNull List<@NonNull Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type, @NonNull final Long node
  );
}
