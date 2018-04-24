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
  /**
   * Chain two transformation into one transformation.
   * 
   * @param innerTransformation The transformation to apply directly on the given input view.
   * @param outerTransformation The transformation to apply directly over the intermediate result view.
   * 
   * @return A chain transformation that apply both transformations and return the result of the chain.
   */
  public static <
    Input extends View<?>, 
    Chain extends View<?>, 
    Output extends View<?>
  > TransformationChain<Input, Chain, Output> chain (
    @NonNull final Transformation<Input, Chain> innerTransformation,
    @NonNull final Transformation<Chain, Output> outerTransformation
  ) {
    return new TransformationChain<>(innerTransformation, outerTransformation);
  }

  @NonNull
  private final Transformation<Input, Chain> _innerTransformation;
  
  @NonNull
  private final Transformation<Chain, Output> _outerTransformation;

  /**
   * Create a new transformation that is the result of a chain of two transformation.
   * 
   * @param innerTransformation The transformation to apply directly on the given input view.
   * @param outerTransformation The transformation to apply directly over the intermediate result view.
   */
  public TransformationChain(
    @NonNull final Transformation<Input, Chain> innerTransformation,
    @NonNull final Transformation<Chain, Output> outerTransformation
  )
  {
    _outerTransformation = outerTransformation;
    _innerTransformation = innerTransformation;
  }

  @Override
  public Output apply (@NonNull final Input input) {
    return _outerTransformation.apply(_innerTransformation.apply(input));
  }
}
