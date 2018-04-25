package org.liara.api.collection.view;

import java.util.List;
import java.util.stream.Collectors;

import org.liara.api.collection.transformation.MapValueTransformation;
import org.springframework.lang.NonNull;

public class MappedListResult<GivenType, ResultType> implements View<List<ResultType>>
{
  @NonNull
  private final View<List<GivenType>> _parentView;
  
  @NonNull
  private final MapValueTransformation<GivenType, ResultType> _transformation;

  public MappedListResult(
    @NonNull final View<List<GivenType>> parentView,
    @NonNull final MapValueTransformation<GivenType, ResultType> transformation
  )
  {
    _parentView = parentView;
    _transformation = transformation;
  }

  @Override
  public List<ResultType> get () {
    return _parentView.get().stream().map(_transformation.getMapper())
                                     .collect(Collectors.toList());
  }
}
