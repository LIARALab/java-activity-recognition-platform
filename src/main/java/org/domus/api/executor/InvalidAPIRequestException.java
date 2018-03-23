package org.domus.api.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.lang.NonNull;

public class InvalidAPIRequestException extends Exception
{
  /**
   * 
   */
  private static final long  serialVersionUID = -8708839431509112962L;

  private List<RequestError> _errors;

  public InvalidAPIRequestException(@NonNull final Collection<RequestError> errors, @NonNull final String message) {
    super(message);
    this._errors = new ArrayList<>(errors);
  }

  public InvalidAPIRequestException(@NonNull final Collection<RequestError> errors) {
    super();
    this._errors = new ArrayList<>(errors);
  }

  public List<RequestError> errors () {
    return this._errors;
  }
}
