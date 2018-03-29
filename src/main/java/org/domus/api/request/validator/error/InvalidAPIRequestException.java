/**
 * Copyright 2018 Cédric DEMONGIVERT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.domus.api.request.validator.error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.domus.api.request.APIRequest;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 */
@JsonIgnoreProperties({ "cause", "message", "stackTrace", "localizedMessage", "suppressed" })
public class InvalidAPIRequestException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @NonNull private final List<APIRequestError> _errors;
  @NonNull private final APIRequest _request;
  
  public InvalidAPIRequestException (
    @NonNull final APIRequest request,
    @NonNull final Iterable<APIRequestError> errors
  ) {
    this._request = APIRequest.immutable(request);
    this._errors = new ArrayList<>();
    Iterables.addAll(_errors, errors);
  }
  
  public InvalidAPIRequestException (
    @NonNull final APIRequest request,
    @NonNull final Iterator<APIRequestError> errors
  ) {
    this._request = APIRequest.immutable(request);
    this._errors = new ArrayList<>();
    Iterators.addAll(_errors, errors);
  }
  
  public InvalidAPIRequestException (
    @NonNull final APIRequest request,
    @NonNull final APIRequestError... errors
  ) {
    this._request = APIRequest.immutable(request);
    this._errors = new ArrayList<>();
    _errors.addAll(Arrays.asList(errors));
  }
  
  public List<APIRequestError> getErrors () {
    return Collections.unmodifiableList(_errors);
  }
  
  public APIRequest getRequest () {
    return this._request;
  }
}
