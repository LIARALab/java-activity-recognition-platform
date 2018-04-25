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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.liara.api.request.APIRequest;
import org.liara.api.request.validator.error.APIRequestError;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class CompoundAPIRequestValidator implements APIRequestValidator
{
  @NonNull
  private final Set<APIRequestValidator> _validators = new HashSet<>();

  public CompoundAPIRequestValidator () {

  }
  
  public CompoundAPIRequestValidator (@NonNull final Iterable<APIRequestValidator> validators) {
    Iterables.addAll(_validators, validators);
  }
  
  public CompoundAPIRequestValidator (@NonNull final Iterator<APIRequestValidator> validators) {
    Iterators.addAll(_validators, validators);
  }
  
  public CompoundAPIRequestValidator (@NonNull final APIRequestValidator... validators) {
    _validators.addAll(Arrays.asList(validators));
  }
  
  @Override
  public List<APIRequestError> validate (@NonNull final APIRequest request) {
    final List<APIRequestError> result = new ArrayList<>();
    
    for (final APIRequestValidator validator : _validators) {
      result.addAll(validator.validate(request));
    }
    
    return result;
  }
}
