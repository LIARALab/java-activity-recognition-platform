package org.liara.api.collection.transformation;

import java.util.function.Function;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.view.EntityCollectionAggregation;
import org.liara.api.collection.view.MapView;
import org.liara.api.collection.view.MappedListResult;
import org.liara.api.collection.view.MappedMapViewValue;
import org.liara.api.collection.view.MappedSingleResult;
import org.springframework.lang.NonNull;

public class MapValueTransformation<GivenType, ResultType>
{
  public static <Type> MapValueTransformation<Type, Type> identity () {
    return new MapValueTransformation<>(x -> x);
  } 
  
  @NonNull
  private final Function<GivenType, ResultType> _mapper;
  
  public MapValueTransformation (
    @NonNull final Function<GivenType, ResultType> mapper
  ) {
    _mapper = mapper;
  }
  
  public MappedSingleResult<GivenType, ResultType> apply (
    @NonNull final EntityCollectionAggregation<?, GivenType> aggregation
  ) {
    return new MappedSingleResult<>(aggregation, this);
  }
  
  public MappedListResult<GivenType, ResultType> apply (
    @NonNull final EntityCollection<GivenType> collection
  ) {
    return new MappedListResult<>(collection, this);
  }
  
  public MappedMapViewValue<GivenType, ResultType> apply (
    @NonNull final MapView collection
  ) {
    return new MappedMapViewValue<>(collection, this);
  }
  
  public Function<GivenType, ResultType> getMapper () {
    return _mapper;
  }
}
