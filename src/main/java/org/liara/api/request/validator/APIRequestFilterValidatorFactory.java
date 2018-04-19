package org.liara.api.request.validator;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.filter.validator.DateTimeFilterValidator;
import org.liara.api.filter.validator.DoubleFilterValidator;
import org.liara.api.filter.validator.DurationFilterValidator;
import org.liara.api.filter.validator.IntegerFilterValidator;
import org.liara.api.filter.validator.TextFilterValidator;
import org.springframework.lang.NonNull;

public final class APIRequestFilterValidatorFactory
{
  public static APIRequestFilterValidator integer (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new IntegerFilterValidator());
  }
  
  public static APIRequestFilterValidator realDouble (@NonNull final String parameter) {
    return new APIRequestFilterValidator(parameter, new DoubleFilterValidator());
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
  
  public static APIRequestValidator joinConfiguration (
    @NonNull final String parameter, 
    @NonNull final Class<? extends CollectionRequestConfiguration<?>> configuration
  ) {
    return new APISubRequestValidator(
      parameter, 
      configuration
    );
  }
  
  public static APIRequestValidator joinCollection (
    @NonNull final String parameter, 
    @NonNull final Class<? extends EntityCollection<?, ?>> configuration
  ) {
    return new APISubRequestValidator(
      parameter, 
      CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
  
  public static APIRequestValidator havingConfiguration (
    @NonNull final String parameter, 
    @NonNull final Class<? extends CollectionRequestConfiguration<?>> configuration
  ) {
    return new APISubRequestValidator(
      parameter, configuration
    );
  }

  public static APIRequestValidator havingCollection (
    @NonNull final String parameter, 
    @NonNull final Class<? extends EntityCollection<?, ?>> configuration
  ) {
    return new APISubRequestValidator(
      parameter, 
      CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
  
  public static APIRequestValidator customHavingConfiguration (
    @NonNull final String parameter, 
    @NonNull final Class<? extends CollectionRequestConfiguration<?>> configuration
  ) {
    return new APISubRequestValidator(
      parameter, configuration
    );
  }

  public static APIRequestValidator customHavingCollection (
    @NonNull final String parameter, 
    @NonNull final Class<? extends EntityCollection<?, ?>> configuration
  ) {
    return new APISubRequestValidator(
      parameter, 
      CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
}
