package org.domus.api;

import org.springframework.lang.NonNull;

public class APIErrorMessage {
  @NonNull private final String _message;
  
  public APIErrorMessage (@NonNull final String message) {
    this._message = message;
  }
  
  public String getMessage () {
    return this._message;
  }
}
