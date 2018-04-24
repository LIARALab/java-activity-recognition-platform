package org.liara.api.collection.transformation;

import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

public class      TransformationChain<
                    Input extends View<?>,
                    Chain extends View<?>, 
                    Output extends View<?>
                  >
       implements Transformation<Input, Output>
{
  public static <
    Input extends View<?>, 
    Chain extends View<?>, 
    Output extends View<?>
  > TransformationChain<Input, Chain, Output> chain (
    @NonNull final Transformation<Input, Chain> up,
    @NonNull final Transformation<Chain, Output> down
  ) {
    return new TransformationChain<>(up, down);
  }

  @NonNull
  private final Transformation<Input, Chain> _upTransformation;
  
  @NonNull
  private final Transformation<Chain, Output> _downTransformation;

  public TransformationChain(
    @NonNull final Transformation<Input, Chain> upTransformation,
    @NonNull final Transformation<Chain, Output> downTransformation
  )
  {
    _upTransformation = upTransformation;
    _downTransformation = downTransformation;
  }

  @Override
  public Output apply (@NonNull final Input input) {
    return _downTransformation.apply(_upTransformation.apply(input));
  }
}
