package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.util.Collection;
import java.util.List;

import org.springframework.lang.NonNull;

public interface ConjunctionToActivitySensorData
{
  public List<Conjunction> getConjunctions (@NonNull final Collection<Long> inputs);
}
