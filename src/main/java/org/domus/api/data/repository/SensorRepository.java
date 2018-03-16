package org.domus.api.data;

import org.springframework.data.repository.CrudRepository;
import java.lang.Integer;

public interface SensorRepository extends CrudRepository<Sensor, Integer> {
}
