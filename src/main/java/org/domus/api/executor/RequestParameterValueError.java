package org.domus.api.executor;

import org.domus.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class RequestParameterValueError extends RequestParameterError
{
  private final int _index;

  public RequestParameterValueError(
    @NonNull final APIRequest request,
    @NonNull final String parameter,
    @NonNull final String message
  )
  {
    super(request, parameter, message);
    this._index = 0;
  }

  public RequestParameterValueError(
    @NonNull final APIRequest request,
    @NonNull final String parameter,
    @NonNull final int index,
    @NonNull final String message
  )
  {
    super(request, parameter, message);
    this._index = index;
  }

  public int getIndex () {
    return this._index;
  }
}
