package org.liara.api.data.repository.database;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.liara.api.data.Conjunction;
import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.ApplicationEntity_;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.state.ActivationState_;
import org.liara.api.data.repository.ConjunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DatabaseConjunctionRepository implements ConjunctionRepository
{
  @NonNull
  private final EntityManager _entityManager;
  
  @Autowired
  public DatabaseConjunctionRepository (
    @NonNull final EntityManager entityManager
  ) {
    _entityManager = entityManager;
  }

  @Override
  public List<Conjunction> getConjunctions (
    @NonNull final Collection<ApplicationEntityReference<Sensor>> inputs
  ) {    
    final CriteriaBuilder builder = _entityManager.getCriteriaBuilder();
    final CriteriaQuery<Tuple> query = builder.createTupleQuery();
    
    final List<Map.Entry<Long, Root<ActivationState>>> selections = new ArrayList<>(
      inputs.stream().collect(
        Collectors.toMap(
          reference -> reference.getIdentifier(),
          reference -> query.from(ActivationState.class)
        )
      ).entrySet()
    );
    
    final Expression<ZonedDateTime> conjunctionEnd = builder.function(
      "LEAST", ZonedDateTime.class,
      selections.stream()
                .map(entry -> entry.getValue().get(ActivationState_._end))
                .collect(Collectors.toList())
                .toArray(new Expression[selections.size()])
    );
    
    final Expression<ZonedDateTime> conjunctionStart = builder.function(
      "GREATEST", ZonedDateTime.class,
      selections.stream()
                .map(entry -> entry.getValue().get(ActivationState_._start))
                .collect(Collectors.toList())
                .toArray(new Expression[selections.size()])
    );
    
    final List<Selection<?>> tupleSelection = new ArrayList<>();
    selections.forEach(x -> tupleSelection.add(x.getValue()));
    
    query.multiselect(tupleSelection);
    
    final List<Predicate> predicates = new ArrayList<>();
    
    for (int index = 0; index < selections.size(); ++index) {
      predicates.add(
        builder.equal(
          selections.get(index)
                    .getValue()
                    .get(ActivationState_._sensor)
                    .get(ApplicationEntity_._identifier),
          selections.get(index).getKey()
        )
      );
      
      predicates.add(
        builder.lessThanOrEqualTo(
          selections.get(index)
                    .getValue()
                    .get(ActivationState_._start),
          conjunctionEnd
        )
      );
      
      predicates.add(
        builder.greaterThanOrEqualTo(
          selections.get(index)
                    .getValue()
                    .get(ActivationState_._end),
          conjunctionStart
        )
      );
    }
    
    query.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
    
    final TypedQuery<Tuple> typedQuery = _entityManager.createQuery(query);
    
    return typedQuery.getResultList()
                     .stream()
                     .map(x -> new Conjunction(x))
                     .collect(Collectors.toList());
  }

}
