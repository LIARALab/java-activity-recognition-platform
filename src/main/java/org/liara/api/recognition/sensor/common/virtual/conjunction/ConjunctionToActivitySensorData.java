package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.util.Collection;
import java.util.List;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.springframework.lang.NonNull;

public interface ConjunctionToActivitySensorData
{
  public List<Conjunction> getConjunctions (
    @NonNull final Collection<ApplicationEntityReference<Sensor>> inputs,
    @NonNull final Collection<ApplicationEntityReference<Node>> nodes
  );
}
