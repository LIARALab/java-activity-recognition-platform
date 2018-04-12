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
        CollectionRequestConfiguration.fromRawClass(_configuration).createValidators()
      ).validate(request);
    } else {
      return Collections.emptyList();
    }
  }
}
