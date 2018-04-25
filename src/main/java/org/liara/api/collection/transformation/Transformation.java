/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
