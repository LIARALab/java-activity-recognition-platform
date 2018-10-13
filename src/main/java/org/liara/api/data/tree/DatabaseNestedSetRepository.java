package org.liara.api.data.tree;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class DatabaseNestedSetRepository
  implements NestedSetRepository
{
  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DatabaseNestedSetRepository (
    @NonNull final EntityManager entityManager
  )
  { _entityManager = entityManager; }

  public void attachNode (
    @NonNull final NestedSet node
  )
  {
    attachChild(node, null);
  }

  public int getSetEnd (@NonNull final Class<? extends NestedSet> entity) {
    @NonNull final TypedQuery<Integer> query = _entityManager.createQuery(String.join(
      "",
      "SELECT MAX(node._coordinates._end) + 1 FROM ",
      entity.getName(),
      " node"
    ), Integer.class);

    final List<Integer> result = query.getResultList();

    return (result.size() <= 0) ? 1 : result.get(0).intValue();
  }

  public void attachChild (@NonNull final NestedSet node) {
    attachChild(node, null);
  }

  @Override
  public void attachChild (
    @NonNull final NestedSet node, @Nullable final NestedSet parent
  )
  {
    if (parent != null) {
      _entityManager.createQuery(String.join("",
                                             "UPDATE ",
                                             node.getClass().getName(),
                                             " node ",
                                             "SET node.coordinates.start = node.coordinates.start + 2 ",
                                             "WHERE node.coordinates.start > :parentSetEnd"
      )).setParameter("parentSetEnd", parent.getCoordinates().getEnd()).executeUpdate();

      _entityManager.createQuery(String.join("",
                                             "UPDATE ",
                                             node.getClass().getName(),
                                             " node ",
                                             "SET node.coordinates.end = node.coordinates.end + 2 ",
                                             "WHERE node.coordinates.end >= :parentSetEnd"
      )).setParameter("parentSetEnd", parent.getCoordinates().getEnd()).executeUpdate();
    }

    @NonNull final Query childCoordinatesQuery = _entityManager.createQuery(String.join("",
                                                                                        "UPDATE ",
                                                                                        node.getClass().getName(),
                                                                                        " node ",
                                                                                        "   SET node.coordinates.end " +
                                                                                        "= :childSetEnd, ",
                                                                                        "       node.coordinates" +
                                                                                        ".start = :childSetStart, ",
                                                                                        "       node.coordinates" +
                                                                                        ".depth = :childSetDepth ",
                                                                                        " WHERE node.identifier = " +
                                                                                        ":childIdentifier"
    )).setParameter("childIdentifier", node.getIdentifier());

    if (parent != null) {
      childCoordinatesQuery.setParameter("childSetEnd", parent.getCoordinates().getEnd() - 1)
                           .setParameter("childSetStart", parent.getCoordinates().getEnd() - 2)
                           .setParameter("childSetDepth", parent.getCoordinates().getDepth() + 1);
    } else {
      final int setEnd = getSetEnd(node.getClass());
      childCoordinatesQuery.setParameter("childSetEnd", setEnd + 1)
                           .setParameter("childSetStart", setEnd)
                           .setParameter("childSetDepth", 1);
    }

    childCoordinatesQuery.executeUpdate();
  }

  @Override
  public <Node extends NestedSet> @NonNull List<@NonNull Node> getAllChildrenOf (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager.createQuery(String.join("",
                                                                                                  "SELECT node ",
                                                                                                  "  FROM ",
                                                                                                  node.getClass()
                                                                                                      .getName(),
                                                                                                  " node ",
                                                                                                  " WHERE node" +
                                                                                                  ".coordinates.start" +
                                                                                                  " > :parentSetStart ",
                                                                                                  "   AND node" +
                                                                                                  ".coordinates.end <" +
                                                                                                  " :parentSetEnd "
    ), node.getClass())
                                                                         .setParameter(
                                                                           "parentSetStart",
                                                                           node.getCoordinates().getStart()
                                                                         )
                                                                         .setParameter(
                                                                           "parentSetEnd",
                                                                           node.getCoordinates().getEnd()
                                                                         );

    return (List<Node>) query.getResultList();
  }

  @Override
  public <Node extends NestedSet> @NonNull List<@NonNull Node> getChildrenOf (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager.createQuery(String.join("",
                                                                                                  "SELECT node ",
                                                                                                  "  FROM ",
                                                                                                  node.getClass()
                                                                                                      .getName(),
                                                                                                  " node ",
                                                                                                  " WHERE node" +
                                                                                                  ".coordinates.start" +
                                                                                                  " > :parentSetStart ",
                                                                                                  "   AND node" +
                                                                                                  ".coordinates.end <" +
                                                                                                  " :parentSetEnd ",
                                                                                                  "   AND node" +
                                                                                                  ".coordinates.depth" +
                                                                                                  " = :parentDepth + 1"
    ), node.getClass())
                                                                         .setParameter(
                                                                           "parentSetStart",
                                                                           node.getCoordinates().getStart()
                                                                         )
                                                                         .setParameter(
                                                                           "parentSetEnd",
                                                                           node.getCoordinates().getEnd()
                                                                         )
                                                                         .setParameter(
                                                                           "parentDepth",
                                                                           node.getCoordinates().getDepth()
                                                                         );

    return (List<Node>) query.getResultList();
  }

  @Override
  public <Node extends NestedSet> @Nullable Node getParentOf (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager.createQuery(String.join("",
                                                                                                  "SELECT node ",
                                                                                                  "  FROM ",
                                                                                                  node.getClass()
                                                                                                      .getName(),
                                                                                                  " node ",
                                                                                                  " WHERE node.coordinates.start < :childSetStart ",
                                                                                                  "   AND node.coordinates.end > :childSetEnd ",
                                                                                                  "   AND node.coordinates.depth = :childDepth - 1"
    ), node.getClass())
                                                                         .setParameter(
                                                                           "childSetStart",
                                                                           node.getCoordinates().getStart()
                                                                         )
                                                                         .setParameter(
                                                                           "childSetEnd",
                                                                           node.getCoordinates().getEnd()
                                                                         )
                                                                         .setParameter(
                                                                           "childDepth",
                                                                           node.getCoordinates().getDepth()
                                                                         );

    @NonNull final List<? extends NestedSet> result = query.getResultList();

    return result.size() == 0 ? null : (Node) result.get(0);
  }

  @Override
  public <Node extends NestedSet> @NonNull List<@NonNull Node> getParentsOf (@NonNull final Node node) {
    @NonNull final TypedQuery<? extends NestedSet> query = _entityManager.createQuery(String.join("",
                                                                                                  "SELECT node ",
                                                                                                  "  FROM ",
                                                                                                  node.getClass()
                                                                                                      .getName(),
                                                                                                  " node ",
                                                                                                  " WHERE node.coordinates.start < :childSetStart ",
                                                                                                  "   AND node.coordinates.end > :childSetEnd "
    ), node.getClass())
                                                                         .setParameter(
                                                                           "childSetStart",
                                                                           node.getCoordinates().getStart()
                                                                         )
                                                                         .setParameter(
                                                                           "childSetEnd",
                                                                           node.getCoordinates().getEnd()
                                                                         );

    return (List<Node>) query.getResultList();
  }

  @Override
  public void removeChild (@NonNull final NestedSet node) {
    _entityManager.createQuery(String.join("",
                                           "UPDATE ",
                                           node.getClass().getName(),
                                           " ",
                                           "SET _coordinates.start = _coordinates.start - :removedLength, ",
                                           "    _coordinates.end = _coordinates.end - :removedLength, ",
                                           "WHERE _coordinates.start > :removedSetEnd"
    ))
                  .setParameter("removedLength", node.getCoordinates().getSize())
                  .setParameter("removedSetEnd", node.getCoordinates().getEnd())
                  .executeUpdate();

    _entityManager.createQuery(String.join("",
                                           "UPDATE ",
                                           node.getClass().getName(),
                                           " ",
                                           "SET _coordinates.end = _coordinates.end - :removedLength, ",
                                           "WHERE _coordinates.end > :removedSetEnd ",
                                           "  AND _coordinates.start < :removedSetStart"
    ))
                  .setParameter("removedLength", node.getCoordinates().getSize())
                  .setParameter("removedSetEnd", node.getCoordinates().getEnd())
                  .setParameter("removedSetStart", node.getCoordinates().getStart())
                  .executeUpdate();

    _entityManager.createQuery(String.join("",
                                           "DELETE ",
                                           node.getClass().getName(),
                                           " node ",
                                           "WHERE node.coordinates.end <= :removedSetEnd ",
                                           "  AND node.coordinates.start >= :removedSetStart"
    ))
                  .setParameter("removedSetStart", node.getCoordinates().getStart())
                  .setParameter("removedSetEnd", node.getCoordinates().getEnd())
                  .executeUpdate();
  }

  @Override
  public <Node extends NestedSet> @NonNull Node getRoot (@NonNull final Node node) {
    return (Node) _entityManager.createQuery(String.join("",
                                                         "SELECT node FROM ",
                                                         node.getClass().getName(),
                                                         " node ",
                                                         " WHERE node.coordinates.end >= :childSetEnd ",
                                                         "   AND node.coordinates.start <= :childSetStart ",
                                                         "   AND node.coordinates.depth = 1"
    ), node.getClass())
                                .setParameter("childSetEnd", node.getCoordinates().getEnd())
                                .setParameter("childSetStart", node.getCoordinates().getStart())
                                .getSingleResult();
  }
}
