package org.liara.api.request.parser.filtering;

import org.liara.api.collection.operator.EntityCollectionCommandBasedFilteringOperator;
import org.liara.api.collection.operator.EntityCollectionEmptyFilteringOperator;
import org.liara.api.collection.operator.EntityCollectionFilteringOperator;
import org.liara.api.filter.interpretor.FilterInterpretor;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class      APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Field> 
       implements APIRequestEntityCollectionFilteringOperatorParser<Entity>
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final FilterInterpretor<Entity, Field> _interpretor;
  
  public APIRequestEntityCollectionCommandBasedFilteringOperatorParser (
    @NonNull final String parameter, 
    @NonNull final FilterInterpretor<Entity, Field> interpretor
  ) {
    _parameter = parameter;
    _interpretor = interpretor;
  }

  @Override
  public EntityCollectionFilteringOperator<Entity> parse (@NonNull final APIRequest request) {
    if (request.contains(_parameter)) {
      return new EntityCollectionCommandBasedFilteringOperator<>(
          request.getParameter(_parameter).getValues(), 
          _interpretor
      );
    } else {
      return new EntityCollectionEmptyFilteringOperator<>();
    }
  }
}
