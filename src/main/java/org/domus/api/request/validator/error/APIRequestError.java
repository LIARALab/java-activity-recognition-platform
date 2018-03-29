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
package org.domus.api.request.validator.error;

import org.domus.api.request.APIRequest;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Any error related to a given API Request.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class APIRequestError
{
  @NonNull
  private final APIRequest _request;
  
  @NonNull
  private final String     _description;

  /**
   * Create a new error for a given API Request.
   * 
   * @param request The invalid request.
   * @param description A description of the error.
   */
  public APIRequestError(@NonNull final APIRequest request, @NonNull final String description) {
    _request = APIRequest.immutable(request);
    _description = description;
  }

  /**
   * Return the description of the error.
   * 
   * @return The description of the error.
   */
  public String getDescription () {
    return _description;
  }

  /**
   * Return the invalid request.
   * 
   * @return The invalid request.
   */
  @JsonIgnore
  public APIRequest getInvalidRequest () {
    return _request;
  }
}
