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
package org.domus.api.request.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.domus.api.request.APIRequest;
import org.domus.api.request.validator.error.APIRequestError;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * Validate all filters for an entity.
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class EntityFilterValidator implements FilterValidator
{
  @NonNull
  private final Set<FilterValidator> _validators;

  public EntityFilterValidator() {
    this._validators = new HashSet<>();
  }

  public EntityFilterValidator(@NonNull final Iterable<FilterValidator> validators) {
    this._validators = new HashSet<>();
    Iterables.addAll(_validators, validators);
  }

  public EntityFilterValidator(@NonNull final Iterator<FilterValidator> validators) {
    this._validators = new HashSet<>();
    Iterators.addAll(_validators, validators);
  }

  public EntityFilterValidator(@NonNull final FilterValidator... validators) {
    this._validators = new HashSet<>();
    _validators.addAll(Arrays.asList(validators));
  }

  /**
   * @see org.domus.api.request.validator.APIRequestValidator#validate(org.domus.api.request.APIRequest)
   */
  @Override
  public List<APIRequestError> validate (@NonNull final APIRequest request) {
    final List<APIRequestError> errors = new ArrayList<>();

    for (final FilterValidator validator : _validators) {
      errors.addAll(validator.validate(request));
    }

    return errors;
  }
}
