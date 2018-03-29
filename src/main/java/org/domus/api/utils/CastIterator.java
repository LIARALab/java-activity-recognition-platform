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
package org.domus.api.utils;

import java.util.Iterator;

import org.springframework.lang.NonNull;

public class CastIterator<Base, Cast> implements Iterator<Cast>
{
  @NonNull private final Iterator<Base> _iterator;

  public CastIterator (
    @NonNull final Iterator<Base> iterator
  ) {
    _iterator = iterator;
  }
  
  public static <Base, Cast extends Base> Iterator<Cast> cast (
    @NonNull final Iterator<Base> iterator
  ) {
    return new CastIterator<Base, Cast>(iterator);
  }

  @Override
  public boolean hasNext () {
    return _iterator.hasNext();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Cast next () {
    return (Cast) _iterator.next();
  }
}
