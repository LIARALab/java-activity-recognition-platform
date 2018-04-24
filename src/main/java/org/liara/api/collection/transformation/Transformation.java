package org.liara.api.collection.transformation;

import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

public interface Transformation<
  Input extends View<?>, 
  Output extends View<?>
> { 
  public Output apply (@NonNull final Input input);
  
  public default <Previous extends View<?>> Transformation<Previous, Output> apply (
    @NonNull final Transformation<Previous, Input> other
  ) {
    return TransformationChain.chain(other, this);
  }
}
