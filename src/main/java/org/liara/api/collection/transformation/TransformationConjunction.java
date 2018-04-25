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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.liara.api.collection.view.View;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class      TransformationConjunction<
                    Input extends View<?>
                  >
       implements Transformation<Input, Input>
{
  @NonNull
  private final List<Transformation<Input, Input>> _transformations = new ArrayList<>();
  
  public TransformationConjunction (
    @NonNull final Iterable<Transformation<Input, Input>> transformations
  ) {
    Iterables.addAll(_transformations, transformations);
  }
  
  public TransformationConjunction (
    @NonNull final Iterator<Transformation<Input, Input>> transformations
  ) {
    Iterators.addAll(_transformations, transformations);
  }
  
  public TransformationConjunction (
    @NonNull final Transformation<Input, Input>[] transformations
  ) {
    _transformations.addAll(Arrays.asList(transformations));
  }

  @Override
  public Input apply (@NonNull final Input collection) {
    Input result = collection;
    
    for (final Transformation<Input, Input> transformation : _transformations) {
      result = transformation.apply(result);
    }
    
    return result;
  }
}
