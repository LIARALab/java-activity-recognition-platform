package org.domus.api.executor;

import org.domus.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class RequestParameterError extends RequestError {
  @NonNull private final String _parameter;

  public RequestParameterError (
    @NonNull final APIRequest request,
    @NonNull final String parameter,
    @NonNull final String message
  ) {
    super(request, message);
    this._parameter = parameter;
  }

  public String getParameter () {
    return this._parameter;
  }
}
