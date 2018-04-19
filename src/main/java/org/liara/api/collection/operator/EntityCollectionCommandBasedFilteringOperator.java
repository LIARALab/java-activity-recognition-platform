package org.liara.api.collection.operator;

import java.util.Arrays;
import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.filter.interpretor.FilterInterpretor;
import org.springframework.lang.NonNull;

public class EntityCollectionCommandBasedFilteringOperator<Entity, Field> implements EntityCollectionFilteringOperator<Entity>
{  
  @NonNull
  private final String[] _commands;
  
  @NonNull
  private final FilterInterpretor<Entity, Field> _interpretor;

  public EntityCollectionCommandBasedFilteringOperator(
    @NonNull final String[] commands,
    @NonNull final FilterInterpretor<Entity, Field> interpretor
  ) {
    _commands = commands;
    _interpretor = interpretor;
  }
   
  @Override
  public void apply (@NonNull final EntityCollectionQuery<Entity, ?> query) {
    final Predicate[] predicates = Arrays.stream(_commands).map(
      command -> _interpretor.execute(command, query)
    ).toArray(size -> new Predicate[size]);
    
    query.andWhere(query.getManager().getCriteriaBuilder().or(predicates));
  }
}
