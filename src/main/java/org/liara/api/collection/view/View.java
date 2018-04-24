/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.liara.api.collection.view;

import org.liara.api.collection.transformation.Transformation;
import org.springframework.lang.NonNull;

/**
 * A view over some datas.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * @param <Result> Result returned by this view.
 */
public interface View<Result>
{
  /**
   * Return the content of this view as a single result.
   *
   * @return The content of this view as a single result.
   */
  public Result get ();
  
  /**
   * Apply a transformation to this view and return a view that is the result of the given transformation.
   * 
   * @param transformation Transformation to apply to this collection.
   * 
   * @return A view that is the result of the given transformation applied to this view.
   */
  public default <OutputEntity, Output extends View<OutputEntity>> Output apply (
    @NonNull final Transformation<View<Result>, Output> transformation
  ) { return transformation.apply(this); }
}
