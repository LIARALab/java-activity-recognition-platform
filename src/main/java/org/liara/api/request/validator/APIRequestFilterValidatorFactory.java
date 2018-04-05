package org.liara.api.request.validator;

import org.liara.api.filter.validator.DateTimeFilterValidator;
import org.liara.api.filter.validator.IntegerFilterValidator;
import org.springframework.lang.NonNull;

public final class APIRequestFilterValidatorFactory
{
  public static APIRequestFilterValidator integer (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new IntegerFilterValidator());
  }
  
  public static APIRequestFilterValidator datetime (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DateTimeFilterValidator());
  }
}
