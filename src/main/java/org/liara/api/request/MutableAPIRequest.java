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
package org.liara.api.request;

import java.lang.Iterable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.NonNull;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * An api request.
 */
public final class MutableAPIRequest implements APIRequest
{
  /**
   * Request parameter.
   */
  @NonNull
  private final BiMap<String, MutableAPIRequestParameter> _parameters;

  /**
   * Create a MutableAPIRequest from a map of parameters.
   * 
   * @param map The map of parameters to use.
   * @return A MutableAPIRequest that contains all the parameters of the given
   *         map.
   */
  public static MutableAPIRequest from (@NonNull final Map<String, String[]> map) {
    final MutableAPIRequest result = new MutableAPIRequest();

    for (Map.Entry<String, String[]> entry : map.entrySet()) {
      result.addValues(entry.getKey(), entry.getValue());
    }

    return result;
  }

  /**
   * Create a MutableAPIRequest from an HTTP request.
   * 
   * @param request The request to use.
   * @return A MutableAPIRequest that contains all parameters of the given HTTP
   *         Request.
   */
  public static MutableAPIRequest from (@NonNull final HttpServletRequest request) {
    return MutableAPIRequest.from(request.getParameterMap());
  }

  /**
   * Create a new empty request.
   */
  public MutableAPIRequest() {
    this._parameters = HashBiMap.create();
  }

  /**
   * @see org.liara.api.request.APIRequest#contains(java.lang.String)
   */
  @Override
  public boolean contains (@NonNull final String name) {
    return this._parameters.containsKey(name);
  }

  /**
   * Add a parameter to this request.
   * 
   * @param parameter The parameter to add.
   */
  public void addParameter (@NonNull final MutableAPIRequestParameter parameter) {
    if (this._parameters.containsKey(parameter.getName()) && this._parameters.get(parameter.getName()) != parameter) {
      throw new Error(
        String.join("", "Unnable to add the parameter ", String.valueOf(parameter), " to the request ", this.toString(), " because the given request already contains a parameter with the same name.")
      );
    }

    if (!this._parameters.containsValue(parameter)) {
      this._parameters.put(parameter.getName(), parameter);
      parameter.setRequest(this);
    }
  }

  /**
   * Add a value to a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param value The value to add to the parameter.
   */
  public void addValue (@NonNull final String name, @NonNull final String value) {
    if (!this._parameters.containsKey(name)) {
      this._parameters.put(name, new MutableAPIRequestParameter(name));
    }

    this._parameters.get(name).addValue(value);
  }

  /**
   * Add some values to a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param values All values to add to the parameter.
   */
  public void addValues (@NonNull final String name, @NonNull final Iterable<String> values) {
    if (!this._parameters.containsKey(name)) {
      this._parameters.put(name, new MutableAPIRequestParameter(name));
    }

    this._parameters.get(name).addValues(values);
  }

  /**
   * Add some values to a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param values All values to add to the parameter.
   */
  public void addValues (@NonNull final String name, @NonNull final String[] values) {
    if (!this._parameters.containsKey(name)) {
      this._parameters.put(name, new MutableAPIRequestParameter(name));
    }

    this._parameters.get(name).addValues(values);
  }

  /**
   * Add some values to a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param values All values to add to the parameter.
   */
  public void addValues (@NonNull final String name, @NonNull final Iterator<String> values) {
    if (!this._parameters.containsKey(name)) {
      this._parameters.put(name, new MutableAPIRequestParameter(name));
    }

    this._parameters.get(name).addValues(values);
  }

  /**
   * Remove a parameter from the request.
   *
   * @param name Name of the parameter to remove.
   */
  public void removeParameter (@NonNull final String name) {
    if (this._parameters.containsKey(name)) {
      MutableAPIRequestParameter oldParameter = this._parameters.remove(name);
      oldParameter.setRequest(null);
    }
  }

  /**
   * Remove a parameter from the request.
   *
   * @param name Name of the parameter to remove.
   */
  public void removeParameter (@NonNull final MutableAPIRequestParameter parameter) {
    if (this._parameters.containsValue(parameter)) {
      MutableAPIRequestParameter oldParameter = this._parameters.remove(parameter.getName());
      oldParameter.setRequest(null);
    }
  }

  /**
   * Remove a value of a parameter of this request by index.
   *
   * @param name Name of the parameter to mutate.
   * @param index Index of the value to remove.
   */
  public void removeValue (@NonNull final String name, final int index) {
    this._parameters.get(name).removeValue(index);
  }

  /**
   * Remove a value of a parameter of this request by reference.
   *
   * @param name Name of the parameter to mutate.
   * @param value Value to remove.
   */
  public void removeValue (@NonNull final String name, @NonNull final String value) {
    this._parameters.get(name).removeValue(value);
  }

  /**
   * Remove values of a parameter of this request by reference.
   *
   * @param name Name of the parameter to mutate.
   * @param values Values to remove.
   */
  public void removeValues (@NonNull final String name, @NonNull final Iterable<String> values) {
    this._parameters.get(name).removeValues(values);
  }

  /**
   * Remove values of a parameter of this request by reference.
   *
   * @param name Name of the parameter to mutate.
   * @param values Values to remove.
   */
  public void removeValues (@NonNull final String name, @NonNull final Iterator<String> values) {
    this._parameters.get(name).removeValues(values);
  }

  /**
   * Remove values of a parameter of this request by reference.
   *
   * @param name Name of the parameter to mutate.
   * @param values Values to remove.
   */
  public void removeValues (@NonNull final String name, @NonNull final String[] values) {
    this._parameters.get(name).removeValues(values);
  }

  /**
   * @see org.liara.api.request.APIRequest#getParameterCount()
   */
  @Override
  public int getParameterCount () {
    return this._parameters.size();
  }

  /**
   * @see org.liara.api.request.APIRequest#getValueCount(java.lang.String)
   */
  @Override
  public int getValueCount (@NonNull final String name) {
    return this._parameters.get(name).getValueCount();
  }

  /**
   * @see org.liara.api.request.APIRequest#getParameter(java.lang.String)
   */
  @Override
  public MutableAPIRequestParameter getParameter (@NonNull final String name) {
    return this.getParameter(name);
  }

  /**
   * @see org.liara.api.request.APIRequest#getValue(java.lang.String, int)
   */
  @Override
  public String getValue (@NonNull final String name, final int index) {
    return this._parameters.get(name).getValue(index);
  }

  /**
   * @see org.liara.api.request.APIRequest#getValues(java.lang.String)
   */
  @Override
  public String[] getValues (@NonNull final String name) {
    return this._parameters.get(name).getValues();
  }

  /**
   * @see org.liara.api.request.APIRequest#getParameters()
   */
  @Override
  public Set<? extends APIRequestParameter> getParameters () {
    return this._parameters.values();
  }

  /**
   * @see org.liara.api.request.APIRequest#iterator()
   */
  @Override
  public Iterator<APIRequestParameter> iterator () {
    return new APIRequestIterator(this);
  }

  @Override
  public APIRequest subRequest (@NonNull final String prefix) {
    final MutableAPIRequest result = new MutableAPIRequest();
    
    for (final APIRequestParameter parameter : this.getParameters()) {
      if (parameter.getName().startsWith(prefix + ".")) {
        result.addValues(parameter.getName().substring(prefix.length() + 1), parameter.getValues());
      }
    }
    
    return result;
  }
}
