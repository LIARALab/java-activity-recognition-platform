package org.domus.api.executor;

import org.domus.api.request.APIRequest;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RequestError
{
  @NonNull
  private final APIRequest _request;
  @NonNull
  private final String     _message;

  public RequestError(@NonNull final APIRequest request, @NonNull final String message) {
    this._request = request;
    this._message = message;
  }

  public String getMessage () {
    return this._message;
  }

  @JsonIgnore
  public APIRequest getRequest () {
    return this._request;
  }
}
