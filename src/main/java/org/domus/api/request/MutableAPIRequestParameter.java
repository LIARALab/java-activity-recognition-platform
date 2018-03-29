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
package org.domus.api.request;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public final class MutableAPIRequestParameter implements APIRequestParameter
{
  @Nullable
  private MutableAPIRequest  _request;
  @NonNull
  private String             _name;
  @NonNull
  private final List<String> _values;
  
  public MutableAPIRequestParameter (@NonNull final String name) {
    _name = name;
    _request = null;
    _values = new ArrayList<>();
  }

  /**
   * @see java.util.Iterator#iterator()
   */
  @Override
  public Iterator<String> iterator () {
    return _values.iterator();
  }

  /**
   * @see org.domus.api.request.APIRequestParameter#getRequest()
   */
  @Override
  public APIRequest getRequest () {
    return _request;
  }
  
  /**
   * Change this parameter's related request.
   * 
   * @throws java.lang.Error when the given request already contains a parameter with the same name as this one.
   * 
   * @param request The new related request to attach.
   */
  public void setRequest (@Nullable final MutableAPIRequest request) {
    if (request != _request) {
      if (_request != null && _request.contains(this._name)) {
        throw new Error(String.join(
          "", 
          "Unnable to change the request of the parameter ", this.toString(), " (",
          String.valueOf(_request), ") by ", String.valueOf(_request), " because the new ",
          " request already contains a parameter nammed ", this._name, "."
        ));
      }
      
      if (_request != null) {
        final MutableAPIRequest oldRequest = _request;
        _request = null;
        oldRequest.removeParameter(this);
      }
      
      _request = request;
      
      if (_request != null) {
        _request.addParameter(this);
      }
    }
  }

  /**
   * @see org.domus.api.request.APIRequestParameter#getName()
   */
  @Override
  public String getName () {
    return _name;
  }
  
  /**
   * Rename this parameter.
   * 
   * @throws java.lang.Error When the parent request of this parameter (if exists) already contains a parameter with the new name.
   * 
   * @param newName The new name of this parameter.
   */
  public void setName (@NonNull final String newName) {
    if (!newName.equals(_name)) {
      if (_request != null && _request.contains(newName)) {
        throw new Error(String.join(
          "", 
          "Unnable to rename the parameter ", this.toString(), " ", this._name, " into ", newName, " because ",
          "the parent request of this parameter (", _request.toString(), ") already contains a parameter nammed ",
          newName, "."
        ));
      }
      
      final MutableAPIRequest request = _request;
      this.setRequest(null);
      this._name = newName;
      this.setRequest(request);
    }
  }

  /**
   * @see org.domus.api.request.APIRequestParameter#getValueCount()
   */
  @Override
  public int getValueCount () {
    return this._values.size();
  }

  /**
   * @see org.domus.api.request.APIRequestParameter#getValue(int)
   */
  @Override
  public String getValue (final int index) {
    return this._values.get(index);
  }
  
  /**
   * Register a value in this parameter.
   * 
   * @param value A value to register.
   */
  public void addValue (@NonNull final String value) {
    this._values.add(value);
  }
  
  /**
   * Register a collection of values in this parameter.
   * 
   * @param values All values to register.
   */
  public void addValues (@NonNull final String[] values) {
    for (final String value : values) {
      this.addValue(value);
    }
  }

  /**
   * Register a collection of values in this parameter.
   * 
   * @param values All values to register.
   */
  public void addValues (@NonNull final Iterable<String> values) {
    Iterables.addAll(this._values, values);
  }

  /**
   * Register a collection of values in this parameter.
   * 
   * @param values All values to register.
   */
  public void addValues (@NonNull final Iterator<String> values) {
    Iterators.addAll(this._values, values);
  }
  
  /**
   * Remove a value of this parameter by index.
   * 
   * @throws java.lang.IndexOutOfBoundsException When the given index is not between 0 and the count of values registered in this parameter.
   * 
   * @param index Index of the value to remove.
   */
  public void removeValue (final int index) {
    this._values.remove(index);
  }
  
  /**
   * Remove a value of this parameter by reference.
   * 
   * @param value The value to remove.
   */
  public void removeValue (@NonNull final String value) {
    this._values.remove(value);
  }
  
  /**
   * Remove a collection of values of this parameter by reference.
   * 
   * @param values All values to remove.
   */
  public void removeValues (@NonNull final String[] values) {
    for (final String value : values) {
      this._values.remove(value);
    }
  }
  
  /**
   * Remove a collection of values of this parameter by reference.
   * 
   * @param values All values to remove.
   */
  public void removeValues (@NonNull final Iterable<String> values) {
    this.removeValues(values.iterator());
  }
  
  /**
   * Remove a collection of values of this parameter by reference.
   * 
   * @param values All values to remove.
   */
  public void removeValues (@NonNull final Iterator<String> values) {
    while (values.hasNext()) {
      this.removeValue(values.next());
    }
  }
  
  /**
   * Remove all values of this parameter.
   */
  public void clear () {
    this._values.clear();
  }

  /**
   * @see org.domus.api.request.APIRequestParameter#getValues()
   */
  @Override
  public String[] getValues () {
    return this._values.toArray(new String[this._values.size()]);
  }
}
