package org.liara.api.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

public class NodeModelController extends ApplicationModelController<Node>
{
  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public NodeModelController (
    @NonNull final EntityManager entityManager
  ) {
    super (Node.class);
    _entityManager = entityManager;
  }

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

  public @NonNull getParentNode (@NonNull final Long identifier) {

  }

  public @NonNull EntityManager getEntityManager () {
    return _entityManager;
  }
}
