package org.liara.api.controller.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.ApplicationModelController;
import org.liara.api.controller.WritableControllerConfiguration;
import org.liara.api.data.entity.Node;
import org.springframework.beans.factory.annotation.Autowired;

public class NodeModelController extends ApplicationModelController<Node>
{
  @NonNull
  private final WritableControllerConfiguration _configuration;

  @Autowired
  public NodeModelController (
    @NonNull final WritableControllerConfiguration configuration
  ) {
    super(Node.class, configuration);

    _configuration = configuration;
  }

  /*
  @Relation.Name("children")
  public @NonNull CollectionRelation<Node> getChildrenCollectionRelation () {
    return this::getChildrenCollection;
  }

  public @NonNull CollectionController<Node> getChildrenCollection (@NonNull final Long identifier) {
    return new ChildrenNodeCollectionController(identifier, _configuration);
  }
  */
}
