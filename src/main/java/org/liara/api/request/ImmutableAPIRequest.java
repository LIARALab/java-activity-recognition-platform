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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.NonNull;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author cedric
 *
 */
public class ImmutableAPIRequest implements APIRequest
{
  @NonNull private final BiMap<String, ImmutableAPIRequestParameter> _parameters;
  
  /**
   * Create a new empty ImmutableAPIRequest instance.
   */
  public ImmutableAPIRequest() {
    _parameters = HashBiMap.create();
  }
  
  /**
   * Create a new deep copy of another APIRequest instance.
   * 
   * @param toCopy A request to copy.
   */
  public ImmutableAPIRequest(@NonNull final APIRequest toCopy) {
    _parameters = HashBiMap.create();
    
    for (final APIRequestParameter parameter : toCopy) {
      _parameters.put(parameter.getName(), new ImmutableAPIRequestParameter(this, parameter));
    }
  }
  
  /**
   * Create a new ImmutableAPIRequest instance with all the parameters stored in a given Map.
   * 
   * @param parameters A map of parameters key, values pair.
   */
  public ImmutableAPIRequest(@NonNull final Map<String, String[]> parameters) {
    _parameters = HashBiMap.create();
    
    for (final Map.Entry<String, String[]> entry : parameters.entrySet()) {
      _parameters.put(
        entry.getKey(), 
        new ImmutableAPIRequestParameter(this, entry.getKey(), entry.getValue())
      );
    }
  }
  
  /**
   * Create a new ImmutableAPIRequest instance with all the parameters from an HttpServletRequest.
   * 
   * @param request An HttpServletRequest to copy.
   */
  public ImmutableAPIRequest(@NonNull final HttpServletRequest request) {
    _parameters = HashBiMap.create();
    
    for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
      _parameters.put(
        entry.getKey(), 
        new ImmutableAPIRequestParameter(this, entry.getKey(), entry.getValue())
      );
    }
  }

  
  /**
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<APIRequestParameter> iterator () {
    return new APIRequestIterator(this);
  }

  /**
   * @see org.liara.api.request.APIRequest#contains(java.lang.String)
   */
  @Override
  public boolean contains (@NonNull final String name) {
    return _parameters.containsKey(name);
  }

  /**
   * @see org.liara.api.request.APIRequest#getParameterCount()
   */
  @Override
  public int getParameterCount () {
    return _parameters.size();
  }

  /**
   * @see org.liara.api.request.APIRequest#getValueCount(java.lang.String)
   */
  @Override
  public int getValueCount (@NonNull final String parameter) {
    return _parameters.get(parameter).getValueCount();
  }

  /**
   * @see org.liara.api.request.APIRequest#getValues(java.lang.String)
   */
  @Override
  public String[] getValues (@NonNull final String name) {
    return _parameters.get(name).getValues();
  }

  /**
   * @see org.liara.api.request.APIRequest#getValue(java.lang.String, int)
   */
  @Override
  public String getValue (@NonNull final String name, final int index) {
    return _parameters.get(name).getValue(index);
  }

  /**
   * @see org.liara.api.request.APIRequest#getParameter(java.lang.String)
   */
  @Override
  public APIRequestParameter getParameter (@NonNull final String name) {
    return _parameters.get(name);
  }

  /**
   * @see org.liara.api.request.APIRequest#getParameters()
   */
  @Override
  public Set<? extends APIRequestParameter> getParameters () {
    return new HashSet<>(_parameters.values());
  }

  /**
   * @see org.liara.api.request.APIRequest#subRequest(java.lang.String)
   */
  @Override
  public APIRequest subRequest (@NonNull final String prefix) {
    final Map<String, String[]> result = new HashMap<>();
    
    for (final APIRequestParameter parameter : this.getParameters()) {
      if (parameter.getName().startsWith(prefix + ".")) {
        result.put(parameter.getName().substring(prefix.length() + 1), parameter.getValues());
      }
    }
    
    return new ImmutableAPIRequest(result);
  }

}
