package org.liara.api.collection.view;

import org.liara.api.collection.transformation.Transformation;
import org.springframework.lang.NonNull;

public abstract class AbstractView<Result> implements View<Result>
{
  @Override
  public <OutputEntity, Output extends View<OutputEntity>> Output apply (
    @NonNull final Transformation<
      View<Result>, Output
    > transformation
  )  {
    return transformation.apply(this);
  }
}
