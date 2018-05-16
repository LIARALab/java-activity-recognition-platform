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
/**
 * 
 */
package org.liara.api.request;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.lang.NonNull;

import com.google.common.collect.Iterators;

/**
 * @author cedric
 *
 */
public class ImmutableAPIRequestParameter implements APIRequestParameter
{
  @NonNull
  private final APIRequest _request;
  
  @NonNull
  private final String     _name;
  
  @NonNull
  private final String[]   _values;

  public ImmutableAPIRequestParameter(
    @NonNull final APIRequest request,
    @NonNull final String name,
    @NonNull final String[] values
  ) {
    _request = request;
    _name = name;
    _values = Arrays.copyOf(values, values.length);
  }

  public ImmutableAPIRequestParameter(
    @NonNull final APIRequest request,
    @NonNull final String name,
    @NonNull final String value
  ) {
    _request = request;
    _name = name;
    _values = new String[] {value};
  }

  public ImmutableAPIRequestParameter(
    @NonNull final APIRequest request,
    @NonNull final String name,
    @NonNull final Collection<String> values
  ) {
    _request = request;
    _name = name;
    _values = values.toArray(new String[values.size()]);
  }
  
  public ImmutableAPIRequestParameter(
    @NonNull final APIRequest request,
    @NonNull final String name,
    @NonNull final Iterable<String> values
  ) {
    _request = request;
    _name = name;
    _values = Iterators.toArray(values.iterator(), String.class);
  }

  public ImmutableAPIRequestParameter(
    @NonNull final APIRequest request,
    @NonNull final String name,
    @NonNull final Iterator<String> values
  ) {
    _request = request;
    _name = name;
    _values = Iterators.toArray(values, String.class);
  }

  public ImmutableAPIRequestParameter(
    @NonNull final APIRequest request,
    @NonNull final APIRequestParameter parameter
  ) {
    _request = request;
    _name = parameter.getName();
    _values = Arrays.copyOf(parameter.getValues(), parameter.getValueCount());
  }
  
  /**
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<String> iterator () {
    return Iterators.forArray(_values);
  }

  /**
   * @see org.liara.api.request.APIRequestParameter#getRequest()
   */
  @Override
  public APIRequest getRequest () {
    return _request;
  }

  /**
   * @see org.liara.api.request.APIRequestParameter#getName()
   */
  @Override
  public String getName () {
    return _name;
  }

  /**
   * @see org.liara.api.request.APIRequestParameter#getValueCount()
   */
  @Override
  public int getValueCount () {
    return _values.length;
  }

  /**
   * @see org.liara.api.request.APIRequestParameter#getValue(int)
   */
  @Override
  public String getValue (final int index) throws IndexOutOfBoundsException {
    return _values[index];
  }

  /**
   * @see org.liara.api.request.APIRequestParameter#getValues()
   */
  @Override
  public String[] getValues () {
    return Arrays.copyOf(_values, _values.length);
  }

}
