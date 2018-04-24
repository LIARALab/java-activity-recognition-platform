package org.liara.api.collection.transformation;

import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

@FunctionalInterface
public interface Transformation<
  Input extends View<?>, 
  Output extends View<?>
> { 
  /**
   * An identity transformation.
   * 
   * The identity transformation return its given view.
   * 
   * @param input View to transform.
   * 
   * @return The given view.
   */
  public static <Input extends View<?>> Input identity (
    @NonNull final Input input
  ) { return input; }
  
  /**
   * Apply the given transformation to another and return a chain transformation.
   * 
   * Return a chain transformation that apply the given transformation to its given view
   * and the second transformation over the intermediate result.
   * 
   * @param other A transformation to chain.
   * 
   * @return A chain transformation that apply the given transformation and then the current transformation.
   */
  public default <Previous extends View<?>> Transformation<Previous, Output> apply (
    @NonNull final Transformation<Previous, Input> other
  ) {
    return TransformationChain.chain(other, this);
  }
  
  /**
   * Transform the given view into another view.
   * 
   * @param input A view to transform.
   * 
   * @return The result of the transformation.
   */
  public Output apply (@NonNull final Input input);
}
