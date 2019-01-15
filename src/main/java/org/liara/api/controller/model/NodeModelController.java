package org.liara.api.controller.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.WritableControllerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class NodeModelController
  extends BaseNodeModelController
{
  @Autowired
  public NodeModelController (
    @NonNull final WritableControllerConfiguration configuration
  ) {
    super(configuration);
  }
}
