package org.liara.api.request.validator;

import java.util.List;

import org.liara.api.request.APIRequest;
import org.liara.api.request.validator.error.APIRequestError;
import org.springframework.lang.NonNull;

public class APISubRequestValidator implements APIRequestValidator
{
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final APIRequestValidator _validator;
  
  public APISubRequestValidator(
    @NonNull final String parameter, 
    @NonNull final APIRequestValidator validator
  ) {
    _parameter = parameter;
    _validator = validator;
  }

  @Override
  public List<APIRequestError> validate (@NonNull final APIRequest request) {
    return _validator.validate(request.subRequest(_parameter));
  }
}
