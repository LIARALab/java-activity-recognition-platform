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

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface APIRequest extends Iterable<APIRequestParameter>
{  
  /**
   * Create an API request from a given HTTP request.
   * 
   * @param request An http request to use for the creation of the APIRequest.
   * @return An API request with all parameters of the given HttpServletRequest.
   */
  public static APIRequest from (@NonNull final HttpServletRequest request) {
    return new ImmutableAPIRequest(request);
  }

  /**
   * Create an API request from a given map request.
   * 
   * @param request An map request to use for the creation of the APIRequest.
   * @return An API request with all parameters of the given map request.
   */
  public static APIRequest from (@NonNull final Map<String, String[]> request) {
    return new ImmutableAPIRequest(request);
  }
  
  /**
   * Return a copy of a request.
   * 
   * @param request Request to copy.
   * @return A copy of the given request.
   */
  public static APIRequest copy (@NonNull final APIRequest request) {
    return new ImmutableAPIRequest(request);
  }
  
  /**
   * Return an immutable request from a request.
   * 
   * @param request Request to transform.
   * @return An immutable request.
   */
  public static APIRequest immutable (@NonNull final APIRequest request) {
    if (request instanceof ImmutableAPIRequest) {
      return request;
    } else {
      return new ImmutableAPIRequest(request);
    }
  }
  
  /**
   * Create and return a mutable APIRequest.
   * 
   * @return A mutable APIRequest instance.
   */
  public static MutableAPIRequest createMutable () {
    return new MutableAPIRequest();
  }
  
  /**
   * Return a subRequest with all parameters that share the same prefix.
   * 
   * @param prefix
   * @return A subRequest with all parameters that share the same prefix.
   */
  public APIRequest subRequest (@NonNull final String prefix);
  
  /**
   * Check if a parameter is registered in this request.
   *
   * @param name The name of the parameter to find.
   *
   * @return True if the given parameter exists in this request, false otherwise.
   */
  public boolean contains (@NonNull final String name);

  /**
   * Return the number of parameter registered in this request.
   *
   * @return The number of parameter registered in this request.
   */
  @JsonIgnore
  public int getParameterCount ();

  /**
   * Return the number of values registered for a given parameter.
   *
   * @param parameter The name of the parameter to check.
   *
   * @return The number of values registered for the given parameter.
   */
  public int getValueCount (@NonNull final String parameter);

  /**
   * Return all values assigned to a parameter of this request.
   *
   * @param name The name of the parameter to find.
   *
   * @return All values assigned to the given parameter.
   */
  public String[] getValues (@NonNull final String name);

  /**
   * Return a value assigned to a parameter of this request.
   *
   * @param name The name of the parameter to find.
   * @param index The index of the value to return.
   *
   * @return The requested value of the given parameter.
   */
  public String getValue (@NonNull final String name, final int index);

  /**
   * Return a parameter of this request.
   *
   * @param name The name of the parameter to find.
   *
   * @return The requested parameter.
   */
  public APIRequestParameter getParameter (@NonNull final String name);

  /**
   * Return all parameters of this request.
   *
   * @return All parameters of this request.
   */
  public Set<? extends APIRequestParameter> getParameters ();

}
