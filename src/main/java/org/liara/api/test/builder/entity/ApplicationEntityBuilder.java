package org.liara.api.test.builder.entity;

import org.liara.api.data.entity.ApplicationEntity;

public class ApplicationEntityBuilder 
       extends BaseApplicationEntityBuilder<ApplicationEntityBuilder, ApplicationEntity>
{
  @Override
  public ApplicationEntityBuilder self () {
    return this;
  }
  
  @Override
  public ApplicationEntity build () {
    final ApplicationEntity result = new ApplicationEntity();
    apply(result);
    return result;
  }
}
