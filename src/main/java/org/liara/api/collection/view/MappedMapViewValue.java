package org.liara.api.collection.view;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.liara.api.collection.transformation.MapValueTransformation;
import org.springframework.lang.NonNull;

public class MappedMapViewValue<GivenType, ResultType> implements View<List<Object[]>>
{
  @NonNull
  private final MapView _parentView;
  
  @NonNull
  private final MapValueTransformation<GivenType, ResultType> _transformation;

  public MappedMapViewValue (
    @NonNull final MapView parentView,
    @NonNull final MapValueTransformation<GivenType, ResultType> transformation
  ) {
    _parentView = parentView;
    _transformation = transformation;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Object[]> get () {
    return _parentView.get().stream().map(x -> {
      final Object[] result = Arrays.copyOf(x, x.length);
      result[1] = _transformation.getMapper().apply((GivenType) result[1]);
      return result;
    }).collect(Collectors.toList());
  }
}
