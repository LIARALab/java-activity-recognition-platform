package org.liara.api.controller.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.ApplicationModelController;
import org.liara.api.controller.WritableControllerConfiguration;
import org.liara.api.controller.model.relation.ChildrenNodeRelation;
import org.liara.api.controller.model.relation.DeepChildrenNodeRelation;
import org.liara.api.controller.model.relation.ParentNodeRelation;
import org.liara.api.controller.model.relation.ParentsNodeRelation;
import org.liara.api.data.entity.Node;
import org.liara.rest.metamodel.relation.RestModelRelation;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseNodeModelController
  extends ApplicationModelController<Node>
{
  @NonNull
  private final WritableControllerConfiguration _configuration;

  @Autowired
  public BaseNodeModelController (
    @NonNull final WritableControllerConfiguration configuration
  )
  {
    super(Node.class, configuration);

    _configuration = configuration;
  }

  @RestModelRelation.Factory("parent")
  public @NonNull RestModelRelation<Node> getParentRelation () {
    return _configuration.getApplicationContext().getBean(ParentNodeRelation.class);
  }

  @RestModelRelation.Factory("parents")
  public @NonNull RestModelRelation<Node> getParentsRelation () {
    return _configuration.getApplicationContext().getBean(ParentsNodeRelation.class);
  }

  @RestModelRelation.Factory("children")
  public @NonNull RestModelRelation<Node> getChildrenRelation () {
    return _configuration.getApplicationContext().getBean(ChildrenNodeRelation.class);
  }

  @RestModelRelation.Factory("deep-children")
  public @NonNull RestModelRelation<Node> getDeepChildrenRelation () {
    return _configuration.getApplicationContext().getBean(DeepChildrenNodeRelation.class);
  }
}
