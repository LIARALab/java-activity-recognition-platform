package org.liara.api.request.validator;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.filter.validator.DateTimeFilterValidator;
import org.liara.api.filter.validator.DurationFilterValidator;
import org.liara.api.filter.validator.IntegerFilterValidator;
import org.liara.api.filter.validator.TextFilterValidator;
import org.springframework.lang.NonNull;

public final class APIRequestFilterValidatorFactory
{
  public static APIRequestFilterValidator integer (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new IntegerFilterValidator());
  }
  
  public static APIRequestFilterValidator datetime (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DateTimeFilterValidator());
  }

  public static APIRequestFilterValidator datetimeInRange (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DateTimeFilterValidator());
  }
  
  public static APIRequestFilterValidator duration (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DurationFilterValidator());
  }
  
  public static APIRequestFilterValidator text (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new TextFilterValidator());
  }
  
  public static APIRequestValidator join (
    @NonNull final String parameter, 
    @NonNull final CollectionRequestConfiguration<?> configuration
  ) {
    return new APISubRequestValidator(
      parameter, 
      new CompoundAPIRequestValidator(configuration.createFilterValidators())
    );
  }
}
