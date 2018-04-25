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
package org.liara.api.request.validator;

import java.util.Collections;
import java.util.List;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.request.APIRequest;
import org.liara.api.request.validator.error.APIRequestError;
import org.springframework.lang.NonNull;

public class APISubRequestValidator implements APIRequestValidator
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final Class<? extends CollectionRequestConfiguration<?>> _configuration;
  
  public APISubRequestValidator(
    @NonNull final String parameter, 
    @NonNull final Class<? extends CollectionRequestConfiguration<?>> configuration
  ) {
    _parameter = parameter;
    _configuration = configuration;
  }

  @Override
  public List<APIRequestError> validate (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.subRequest(_parameter);
    
    if (subRequest.getParameterCount() > 0) {
      return new CompoundAPIRequestValidator(
        CollectionRequestConfiguration.fromRawClass(_configuration)
                                      .createFilteringValidators()
      ).validate(subRequest);
    } else {
      return Collections.emptyList();
    }
  }
}
