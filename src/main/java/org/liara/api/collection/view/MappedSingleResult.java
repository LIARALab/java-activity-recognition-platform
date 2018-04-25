package org.liara.api.collection.view;

import org.liara.api.collection.transformation.MapValueTransformation;
import org.springframework.lang.NonNull;

public class MappedSingleResult<GivenType, ResultType> implements View<ResultType>
{
  @NonNull
  private final View<GivenType> _parentView;
  
  @NonNull
  private final MapValueTransformation<GivenType, ResultType> _transformation;

  public MappedSingleResult(
    @NonNull final View<GivenType> parentView,
    @NonNull final MapValueTransformation<GivenType, ResultType> transformation
  ) {
    _parentView = parentView;
    _transformation = transformation;
  }

  @Override
  public ResultType get () {
    return _transformation.getMapper().apply(_parentView.get());
  }
}
