package org.liara.api.test.collection.configuration;

import org.springframework.lang.NonNull;

public class CollectionRequestConfigurationFieldValidator<Entity>
{
  @NonNull
  private final CollectionRequestConfigurationValidator<Entity> _parent;
  
  @NonNull
  private final String _field;
  
  @NonNull
  private boolean _isValid = true;
  
  public CollectionRequestConfigurationFieldValidator (
    @NonNull final CollectionRequestConfigurationValidator<Entity> parent,
    @NonNull final String field
  ) {
    _parent = parent;
    _field = field;
  }
  
  public CollectionRequestConfigurationFieldValidator<Entity> asAnIntegerFilter () {
    if (_isValid) {
      
    }
    
    return this;
  }

  public boolean isValid () {
    return _isValid;
  }
}
