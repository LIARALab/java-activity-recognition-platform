package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import java.util.List;

public interface SensorRepository extends ApplicationEntityRepository<Sensor>
{
  default @NonNull List<@NonNull Sensor> getSensorsOfType (
    @NonNull final Class<?> type
  )
  { return getSensorsOfType(type.getName()); }

  @NonNull List<@NonNull Sensor> getSensorsOfType (@NonNull final String type);

  default @NonNull List<@NonNull Sensor> getSensorsOfTypeIntoNode (
    @NonNull final Class<?> type, @NonNull final ApplicationEntityReference<? extends Node> node
  )
  { return getSensorsOfTypeIntoNode(type.getName(), node); }

  @NonNull List<@NonNull Sensor> getSensorsOfTypeIntoNode (
    @NonNull final String type, @NonNull final ApplicationEntityReference<? extends Node> node
  );
}
