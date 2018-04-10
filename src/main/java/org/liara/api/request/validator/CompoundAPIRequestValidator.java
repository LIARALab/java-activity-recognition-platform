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
