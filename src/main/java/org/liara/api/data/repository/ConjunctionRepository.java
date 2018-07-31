package org.liara.api.data.repository;

import java.util.Collection;
import java.util.List;

import org.liara.api.data.Conjunction;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.lang.NonNull;

public interface ConjunctionRepository
{
  public List<Conjunction> getConjunctions (
    @NonNull final Collection<ApplicationEntityReference<Sensor>> inputs
  );
}
