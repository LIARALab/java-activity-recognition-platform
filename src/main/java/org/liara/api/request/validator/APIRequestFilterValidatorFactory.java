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

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.filter.validator.DateTimeFilterValidator;
import org.liara.api.filter.validator.DoubleFilterValidator;
import org.liara.api.filter.validator.DurationFilterValidator;
import org.liara.api.filter.validator.IntegerFilterValidator;
import org.liara.api.filter.validator.TextFilterValidator;
import org.springframework.lang.NonNull;

public final class APIRequestFilterValidatorFactory
{
  public static APIRequestFilterValidator integer (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new IntegerFilterValidator());
  }
  
  public static APIRequestFilterValidator realDouble (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DoubleFilterValidator());
  }
  
  public static APIRequestFilterValidator datetime (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DateTimeFilterValidator());
  }

  public static APIRequestFilterValidator datetimeInRange (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DateTimeFilterValidator());
  }
  
  public static APIRequestFilterValidator duration (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DurationFilterValidator());
  }
  
  public static APIRequestFilterValidator text (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new TextFilterValidator());
  }
  
  public static APIRequestValidator includeConfiguration (
    @NonNull final String parameter, 
    @NonNull final Class<? extends CollectionRequestConfiguration<?>> configuration
  ) {
    return new APISubRequestValidator(
      parameter, 
      configuration
    );
  }
  
  public static APIRequestValidator includeCollection (
    @NonNull final String parameter, 
    @NonNull final Class<? extends EntityCollection<?>> configuration
  ) {
    return new APISubRequestValidator(
      parameter, 
      CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
}
