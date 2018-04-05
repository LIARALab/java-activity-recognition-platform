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
package org.liara.api.request.validator.error;

import org.liara.api.request.APIRequest;
import org.liara.api.request.APIRequestParameter;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Any error related to a parameter of a given API Request.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class APIRequestParameterError extends APIRequestError
{
  @NonNull
  private final String _parameter;
  
  /**
   * Create an APIRequestParameterError for a given parameter.
   * 
   * @param parameter The invalid parameter.
   * @param description A description of the error.
   * 
   * @return A new APIRequestParameterError instance.
   */
  public static APIRequestParameterError create (
    @NonNull final APIRequestParameter parameter,
    @NonNull final String description
  ) {
    return new APIRequestParameterError(
      parameter.getRequest(), 
      parameter.getName(), 
      description
    );
  }

  /**
   * Construct a new error to a parameter of a given API Request.
   * 
   * @param request The invalid request.
   * @param parameter The invalid parameter type.
   * @param description A description of the error.
   */
  protected APIRequestParameterError(
    @NonNull final APIRequest request,
    @NonNull final String parameter,
    @NonNull final String description
  )
  {
    super(request, description);
    
    this._parameter = parameter;
  }

  /**
   * Return the invalid parameter.
   * 
   * @return The invalid parameter.
   */
  @JsonIgnore
  public APIRequestParameter getInvalidParameter () {
    return this.getInvalidRequest().getParameter(this._parameter);
  }
  
  /**
   * Return the name of the invalid parameter.
   * 
   * @return The name of the invalid parameter.
   */
  public String getInvalidParameterName () {
    return this._parameter;
  }
}
