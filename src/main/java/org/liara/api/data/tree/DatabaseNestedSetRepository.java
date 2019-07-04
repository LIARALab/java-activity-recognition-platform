package org.liara.api.data.tree;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class DatabaseNestedSetRepository
  implements NestedSetRepository
{
  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseNestedSetRepository (@NonNull final EntityManager entityManager) {
    _entityManager = entityManager;
  }

  public void attachNode (@NonNull final NestedSet node) {
    attachChild(node, null);
  }

  public void attachChild (@NonNull final NestedSet node) {
    attachChild(node, null);
  }

  @Override
  public void attachChild (
    @NonNull final NestedSet node,
    @Nullable final NestedSet parent
  ) {
    if (node == parent) {
      throw new Error("Unable to attach a node to itself.");
    }

    if (parent == null) {
      @NonNull final Integer setStart = getRootNestedSetStart(node);
      node.getCoordinates().setStart(setStart);
      node.getCoordinates().setEnd(setStart + 1);
      node.getCoordinates().setDepth(1);
    } else {
      expand(parent);

      node.getCoordinates().setStart(parent.getCoordinates().getEnd() - 2);
      node.getCoordinates().setEnd(parent.getCoordinates().getEnd() - 1);
      node.getCoordinates().setDepth(parent.getCoordinates().getDepth() + 1);
    }

    _entityManager.merge(node);
    _entityManager.flush();
  }

  public @NonNull Integer getRootNestedSetStart (@NonNull final NestedSet toAdd) {
    @NonNull final TypedQuery<Integer> query = _entityManager.createQuery(
      "SELECT COALESCE(MAX(node.coordinates.end)," +
      " 0) + 1 " + "FROM " + toAdd.getClass().getName() + " node " +
      "WHERE node.identifier != :toAddIdentifier",
      Integer.class
    );

    query.setParameter("toAddIdentifier", toAdd.getIdentifier());

    return query.getSingleResult();
  }

  private void expand (@NonNull final NestedSet parent) {
    _entityManager.createQuery(
      "UPDATE " + parent.getClass().getName() + " node " + "SET node.coordinates.start = node.coordinates.start + 2 " +
      "WHERE node.coordinates.start > :parentSetEnd")
      .setParameter("parentSetEnd", parent.getCoordinates().getEnd())
      .executeUpdate();

    _entityManager.createQuery(
      "UPDATE " + parent.getClass().getName() + " node " + "SET node.coordinates.end = node.coordinates.end + 2 " +
      "WHERE node.coordinates.end >= :parentSetEnd")
      .setParameter("parentSetEnd", parent.getCoordinates().getEnd())
      .executeUpdate();

    _entityManager.flush();
    _entityManager.refresh(parent);
  }

  @Override
  public <Node extends NestedSet> @NonNull List<@NonNull Node> getAllChildrenOf (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager
                                                             .createQuery(String.join(
                                                               "",
      "SELECT node ",
      "  FROM ",
      node.getClass().getName(),
      " node ",
      " WHERE node" + ".coordinates.start" + " > :parentSetStart ",
      "   AND node" + ".coordinates.end <" + " :parentSetEnd "
    ), node.getClass()).setParameter("parentSetStart", node.getCoordinates().getStart()).setParameter("parentSetEnd",
      node.getCoordinates().getEnd()
    );

    return (List<Node>) query.getResultList();
  }

  @Override
  public <Node extends NestedSet> @NonNull List<@NonNull Node> getChildrenOf (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager
                                                             .createQuery(String.join(
                                                               "",
      "SELECT node ",
      "  FROM ",
      node.getClass().getName(),
      " node ",
      " WHERE node" + ".coordinates.start" + " > :parentSetStart ",
      "   AND node" + ".coordinates.end <" + " :parentSetEnd ",
      "   AND node" + ".coordinates.depth" + " = :parentDepth + 1"
    ), node.getClass()).setParameter("parentSetStart", node.getCoordinates().getStart()).setParameter("parentSetEnd",
      node.getCoordinates().getEnd()
    ).setParameter("parentDepth", node.getCoordinates().getDepth());

    return (List<Node>) query.getResultList();
  }

  @Override
  public <Node extends NestedSet> @Nullable Node getParentOf (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager
                                                             .createQuery(String.join(
                                                               "",
      "SELECT node ",
      "  FROM ",
      node.getClass().getName(),
      " node ",
      " WHERE node.coordinates.start < :childSetStart ",
      "   AND node.coordinates.end > :childSetEnd ",
      "   AND node.coordinates.depth = :childDepth - 1"
    ), node.getClass()).setParameter("childSetStart", node.getCoordinates().getStart()).setParameter("childSetEnd",
      node.getCoordinates().getEnd()
    ).setParameter("childDepth", node.getCoordinates().getDepth());

    @NonNull final List<? extends NestedSet> result = query.getResultList();

    return result.size() == 0 ? null : (Node) result.get(0);
  }

  @Override
  public <Node extends NestedSet> @NonNull List<@NonNull Node> getParentsOf (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager
                                                             .createQuery(String.join(
                                                               "",
      "SELECT node ",
      "  FROM ",
      node.getClass().getName(),
      " node ",
      " WHERE node.coordinates.start < :childSetStart ",
      "   AND node.coordinates.end > :childSetEnd "
    ), node.getClass()).setParameter("childSetStart", node.getCoordinates().getStart()).setParameter("childSetEnd",
      node.getCoordinates().getEnd()
    );

    return (List<Node>) query.getResultList();
  }

  @Override
  public void removeChild (@NonNull final NestedSet node) {
    _entityManager
      .createQuery(String.join("", "UPDATE ", node.getClass().getName(), " ",
      "SET coordinates.start = coordinates.start - :removedLength, ",
      "    coordinates.end = coordinates.end - :removedLength, ",
      "WHERE coordinates.start > :removedSetEnd"
    )).setParameter("removedLength", node.getCoordinates().getSize()).setParameter("removedSetEnd",
      node.getCoordinates().getEnd()
    ).executeUpdate();

    _entityManager
      .createQuery(String.join("", "UPDATE ", node.getClass().getName(), " ",
      "SET coordinates.end = coordinates.end - :removedLength, ",
      "WHERE coordinates.end > :removedSetEnd ",
      "  AND coordinates.start < :removedSetStart"
    ))
      .setParameter("removedLength", node.getCoordinates().getSize())
      .setParameter("removedSetEnd", node.getCoordinates().getEnd())
      .setParameter("removedSetStart", node.getCoordinates().getStart())
      .executeUpdate();

    _entityManager.createQuery(String.join(
      "",
      "DELETE ",
      node.getClass().getName(),
      " node ",
      "WHERE node.coordinates.end <= :removedSetEnd ",
      "  AND node.coordinates.start >= :removedSetStart"
    )).setParameter("removedSetStart", node.getCoordinates().getStart()).setParameter("removedSetEnd",
      node.getCoordinates().getEnd()
    ).executeUpdate();

    _entityManager.flush();
  }

  @Override
  public <Node extends NestedSet> @NonNull Node getRoot (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager.createQuery(
      "SELECT node FROM " + node.getClass().getName() + " node" +
      " WHERE node.coordinates.end >= :childSetEnd " +
      "   AND node.coordinates.start <= :childSetStart " +
      "   AND node.coordinates.depth = 1",
      node.getClass()
    );

    query.setParameter("childSetEnd", node.getCoordinates().getEnd());
    query.setParameter("childSetStart", node.getCoordinates().getStart());
    query.setMaxResults(1);

    return (Node) query.getSingleResult();
  }
}
