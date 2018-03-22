package org.domus.api.executor;

import java.util.List;

import org.springframework.lang.NonNull;

import org.domus.api.request.APIRequest;

/**
* An object that interpret an api request and execute actions.
*/
public interface Executor {
  public void execute (@NonNull final APIRequest request);
  public List<RequestError> validate (@NonNull final APIRequest request);
}
