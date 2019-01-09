package org.liara.api.controller.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.ApplicationModelController;
import org.liara.api.controller.ReadableControllerConfiguration;
import org.liara.api.data.entity.Node;
import org.springframework.beans.factory.annotation.Autowired;

public class NodeModelController extends ApplicationModelController<Node>
{
  @NonNull
  private final ReadableControllerConfiguration _configuration;

  @Autowired
  public NodeModelController (
    @NonNull final ReadableControllerConfiguration configuration
  ) {
    super(Node.class, configuration);

    _configuration = configuration;
  }

  /*
  public @NonNull Operator getChildrenCollectionFilter (@NonNull final Long identifier) {
    @NonNull final Node parent = _entityManager.find(Node.class, identifier);

    return Composition.of(
      Filter.expression(":this.coordinates.start > :parentStart")
            .setParameter("parentStart", parent.getCoordinates().getStart()),
      Filter.expression(":this.coordinates.end < :parentEnd")
            .setParameter("parentEnd", parent.getCoordinates().getEnd()),
      Filter.expression(":this.coordinates.depth = :parentDepth - 1")
            .setParameter("parentDepth", parent.getCoordinates().getDepth())
    );
  }

  public @NonNull Operator getChildrenCollectionFilter () {
    return Composition.of(
      Filter.expression(":this.coordinates.start > :super.coordinates.start"),
      Filter.expression(":this.coordinates.end < :super.coordinates.end"),
      Filter.expression(":this.coordinates.depth = :super.coordinates.depth")
    );
  }
  */
}
