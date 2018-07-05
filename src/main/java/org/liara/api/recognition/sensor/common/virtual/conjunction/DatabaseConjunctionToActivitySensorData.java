package org.liara.api.recognition.sensor.common.virtual.conjunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.liara.api.data.entity.state.ActivationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DatabaseConjunctionToActivitySensorData implements ConjunctionToActivitySensorData
{
  @NonNull
  private final EntityManager _entityManager;
  
  @Autowired
  public DatabaseConjunctionToActivitySensorData (
    @NonNull final EntityManager entityManager
  ) {
    _entityManager = entityManager;
  }

  @Override
  public List<Conjunction> getConjunctions (
    @NonNull final Collection<Long> inputs
  ) {
    final List<String> selections = inputs.stream().map(
      sensorIdentifier -> "statesOfSensor" + sensorIdentifier 
    ).collect(Collectors.toList());
    
    final List<Long> sensors = new ArrayList<>(inputs);
    
    final StringBuilder query = new StringBuilder();
    query.append("SELECT NEW ");
    query.append(Conjunction.class.getName());
    query.append("(");
    
    for (int index = 0; index < selections.size(); ++index) {
      if (index > 0) query.append(", ");
      query.append(selections.get(index));
    }
    
    query.append(")");
    query.append(" FROM ");
    
    for (int index = 0; index < selections.size(); ++index) {
      if (index > 0) query.append(", ");
      query.append(ActivationState.class.getName());
      query.append(" ");
      query.append(selections.get(index));
    }
    
    query.append(", LEAST(");
    for (int index = 0; index < selections.size(); ++index) {
      if (index > 0) query.append(", ");
      query.append(selections.get(index));
      query.append("._end");
    }    
    query.append(") end ");
    
    query.append(", GREATEST(");
    for (int index = 0; index < selections.size(); ++index) {
      if (index > 0) query.append(", ");
      query.append(selections.get(index));
      query.append("._start");
    }    
    query.append(") start ");
    
    query.append(" WHERE ");
    
    for (int index = 0; index < selections.size(); ++index) {
      if (index > 0) query.append(" AND ");
      query.append(selections.get(index));
      query.append("._sensor._identifier = :identifierOfSensor");
      query.append(sensors.get(index));

      query.append(" AND ");
      query.append(selections.get(index));
      query.append("._start <= end");
      
      query.append(" AND ");
      query.append(selections.get(index));
      query.append("._end >= start");
    }
    
    final TypedQuery<Conjunction> typedQuery = _entityManager.createQuery(
      query.toString(), Conjunction.class
    );
    
    for (int index = 0; index < selections.size(); ++index) {
      typedQuery.setParameter(
        "identifierOfSensor" + sensors.get(index), 
        sensors.get(index)
      );
    } 
    
    return typedQuery.getResultList();
  }

}
