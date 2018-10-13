package org.liara.api.data.repository;

import org.liara.api.data.Conjunction;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public interface ConjunctionRepository
{
  public List<Conjunction> getConjunctions (
    @NonNull final Collection<ApplicationEntityReference<Sensor>> inputs
  );
}
