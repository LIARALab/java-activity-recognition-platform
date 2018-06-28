package org.liara.api.collection.transformation;

import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

/**
 * An identity transformation.
 * 
 * The identity transformation of a view, is the view.
 * The identity transformation applied to any other compatible transformation, is that transformation.
 * 
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 *
 * @param <Input> The input type of the identity transformation.
 */
public class IdentityTransformation<Input extends View<?>> implements Transformation<Input, Input>
{  
  public static <T extends View<?>> IdentityTransformation<T> instance () {
    return new IdentityTransformation<>();
  }
  
  private IdentityTransformation () { }
  
  @Override
  public Input apply (@NonNull final Input input) {
    return input;
  }

  /**
   * @see Transformation#apply(Transformation)
   */
  @Override
  public <Previous extends View<?>> Transformation<Previous, Input> apply (
    @NonNull final Transformation<Previous, Input> other
  ) {
    return other;
  }
}
