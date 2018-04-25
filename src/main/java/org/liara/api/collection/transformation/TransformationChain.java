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