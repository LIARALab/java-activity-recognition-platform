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
package org.liara.api.request.validator;

import java.util.ArrayList;
import java.util.List;

import org.liara.api.filter.validator.FilterValidator;
import org.liara.api.request.APIRequest;
import org.liara.api.request.APIRequestParameter;
import org.liara.api.request.validator.error.APIRequestError;
import org.liara.api.request.validator.error.APIRequestParameterValueError;
import org.springframework.lang.NonNull;

public class APIRequestFilterValidator implements APIRequestValidator
{
  @NonNull
  private final FilterValidator _validator;
  
  @NonNull
  private final String _parameter;

  public APIRequestFilterValidator(@NonNull final String parameter, @NonNull final FilterValidator validator) {
    _validator = validator;
    _parameter = parameter;
  }

  @Override
  public List<APIRequestError> validate (@NonNull final APIRequest request) {
    final List<APIRequestError> errors = new ArrayList<>();

    if (request.contains(_parameter)) {
      APIRequestParameter parameter = request.getParameter(_parameter);

      for (int index = 0; index < parameter.getValueCount(); ++index) {
        this.assertIsValidParameterValue(errors, parameter, index);
      }
    }

    return errors;
  }

  private void assertIsValidParameterValue (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    final int index
  ) {
    final List<String> messages = _validator.validate(parameter.getValue(index));
    
    for (final String message : messages) {
      errors.add(APIRequestParameterValueError.create(parameter, index, message));
    }
  }

}
