package org.liara.api.request.parser;

import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class APIRequestDefaultValueParser<Output> implements APIRequestParser<Output>
{
  @NonNull private final String _parameter;
  
  @NonNull private final Output _defaultValue;
  
  @NonNull private final APIRequestParser<Output> _wrapped;

  public APIRequestDefaultValueParser(
    @NonNull final String parameter, 
    @NonNull final Output defaultValue, 
    @NonNull final APIRequestParser<Output> wrapped
  ) {
    _parameter = parameter;
    _defaultValue = defaultValue;
    _wrapped = wrapped;
  }
  
  @Override
  public Output parse (@NonNull final APIRequest request) {
    if (request.contains(_parameter)) {
      return _wrapped.parse(request);
    } else {
      return _defaultValue; 
    }
  }
}
