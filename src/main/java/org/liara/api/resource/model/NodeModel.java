package org.liara.api.resource.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.resource.ModelResource;
import org.liara.api.resource.collection.ChildrenNodeCollectionBuilder;
import org.liara.api.resource.collection.DeepChildrenNodeCollectionBuilder;
import org.liara.api.resource.collection.ParentNodeCollectionBuilder;
import org.liara.api.utils.Builder;
import org.liara.api.utils.Duplicator;
import org.liara.rest.metamodel.RestResource;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.NoSuchElementException;

public class NodeModel
  extends ModelResource<Node>
{
  @NonNull
  private final ParentNodeCollectionBuilder _parentNodeCollectionBuilder;

  @NonNull
  private final DeepChildrenNodeCollectionBuilder _deepChildrenNodeCollectionBuilder;

  @NonNull
  private final ChildrenNodeCollectionBuilder _childrenNodeCollectionBuilder;

  @NonNull
  private final NodeModelBuilder _nodeModelBuilder;

  @NonNull
  private final EntityManager _entityManager;

  public NodeModel (@NonNull final NodeModelBuilder builder) {
    super(Node.class, Duplicator.duplicate(Builder.require(builder.getNode())));
    _parentNodeCollectionBuilder = Builder.require(builder.getParentNodeCollectionBuilder());
    _deepChildrenNodeCollectionBuilder = Builder.require(
      builder.getDeepChildrenNodeCollectionBuilder()
    );
    _childrenNodeCollectionBuilder = Builder.require(builder.getChildrenNodeCollectionBuilder());
    _nodeModelBuilder = Duplicator.duplicate(builder);
    _entityManager = Builder.require(builder.getEntityManager());
  }

  @Override
  public boolean hasResource (@NonNull final String name) {
    return "parent".equalsIgnoreCase(name) ||
           "parents".equalsIgnoreCase(name) ||
           "children".equalsIgnoreCase(name) ||
           "deep-children".equalsIgnoreCase(name);
  }

  @Override
  public @NonNull RestResource getResource (
    @NonNull final String name
  )
  throws NoSuchElementException {
    if ("parent".equalsIgnoreCase(name)) {
      return getParentResource();
    }

    if ("parents".equalsIgnoreCase(name)) {
      _parentNodeCollectionBuilder.setTarget(getModel());
      return _parentNodeCollectionBuilder.build();
    }

    if ("children".equalsIgnoreCase(name)) {
      _childrenNodeCollectionBuilder.setTarget(getModel());
      return _childrenNodeCollectionBuilder.build();
    }

    if ("deep-children".equalsIgnoreCase(name)) {
      _deepChildrenNodeCollectionBuilder.setTarget(getModel());
      return _deepChildrenNodeCollectionBuilder.build();
    }

    return super.getResource(name);
  }

  public @NonNull NodeModel getParentResource ()
  throws NoSuchElementException {
    @NonNull final List<@NonNull Node> nodes = _entityManager.createQuery(
      "SELECT parent " +
      "FROM " + Node.class + " node " +
      "WHERE node.coordinates.start < :start " +
      "  AND node.coordinates.end > :end " +
      "  AND node.coordinates.depth = :depth - 1",
      Node.class
    ).setParameter(
      "start",
      getModel().getCoordinates().getStart()
    ).setParameter(
      "end",
      getModel().getCoordinates().getEnd()
    ).setParameter(
      "depth",
      getModel().getCoordinates().getDepth()
    ).setMaxResults(1).getResultList();

    if (nodes.isEmpty()) {
      throw new NoSuchElementException(
        "Unable to find a parent for the model " + getModel().getIdentifier() + "."
      );
    } else {
      _nodeModelBuilder.setNode(nodes.get(0));
      return _nodeModelBuilder.build();
    }
  }
}
