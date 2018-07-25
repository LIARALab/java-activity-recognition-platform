package org.liara.api.data.entity.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

public class DatabaseNestedSetTree<TreeNode extends NestedSetTreeNode<TreeNode>> 
       implements NestedSetTree<TreeNode>
{
  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final Class<TreeNode> _entity;
  
  @Autowired
  public DatabaseNestedSetTree(
    @NonNull final EntityManager entityManager,
    @NonNull final Class<TreeNode> entity
  ) {
    _entityManager = entityManager;
    _entity = entity;
  }

  @Override
  public void addNode (@NonNull final TreeNode node) {
    addNode(node, null);
  }

  @Override
  public void addNode (@NonNull final TreeNode node, @NonNull final TreeNode parent) {
    if (parent != null) {
      _entityManager.createQuery(
        String.join(
          "", 
          "UPDATE ", _entity.getName(), " node ", 
          "SET node._coordinates._start = node._coordinates._start + 2 ",
          "WHERE node._coordinates._start > :parentSetEnd"
        )
      ).setParameter("parentSetEnd", parent.getCoordinates().getEnd())
       .executeUpdate();
      
      _entityManager.createQuery(
        String.join(
          "", 
          "UPDATE ", _entity.getName(), " node ", 
          "SET node._coordinates._end = node._coordinates._end + 2 ",
          "WHERE node._coordinates._end >= :parentSetEnd"
        )
      ).setParameter("parentSetEnd", parent.getCoordinates().getEnd())
       .executeUpdate();
    }
    
    _entityManager.persist(node);
    
    final Query childCoordinatesQuery = _entityManager.createQuery(
      String.join(
        "", 
        "UPDATE ", _entity.getName(), " node ", 
        "   SET node._coordinates._end = :childSetEnd, ",
        "       node._coordinates._start = :childSetStart, ",
        "       node._coordinates._depth = :childSetDepth ",
        " WHERE node._identifier = :childIdentifier"
      )
    ).setParameter("childIdentifier", node.getIdentifier());
    
    if (parent != null) {
      childCoordinatesQuery
        .setParameter("childSetEnd", parent.getCoordinates().getEnd() - 1)
        .setParameter("childSetStart", parent.getCoordinates().getEnd() - 2)
        .setParameter("childSetDepth", parent.getCoordinates().getDepth() + 1);
    } else {
      childCoordinatesQuery
        .setParameter("childSetEnd", getSetEnd() + 1)
        .setParameter("childSetStart", getSetEnd())
        .setParameter("childSetDepth", 1);
    }
    
    childCoordinatesQuery.executeUpdate();
  }

  @Override
  public void clear () {
    _entityManager.createQuery(String.join("", "DELETE ", _entity.getName())).executeUpdate();
  }

  @Override
  public boolean contains (@NonNull final TreeNode node) {
    if (node.getIdentifier() == null) return false;
    
    return _entityManager.find(_entity, node.getIdentifier()) != null;
  }

  @Override
  public Set<TreeNode> getAllChildrenOf (@NonNull final TreeNode node) {
    final TypedQuery<TreeNode> query = _entityManager.createQuery(
      String.join(
        "", 
        "SELECT node ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE node._coordinates._start > :parentSetStart ",
        "   AND node._coordinates._end < :parentSetEnd "
      ),
      _entity
    ).setParameter("parentSetStart", node.getCoordinates().getStart())
     .setParameter("parentSetEnd", node.getCoordinates().getEnd());
    
    return new HashSet<TreeNode>(query.getResultList());
  }

  @Override
  public Set<TreeNode> getChildrenOf (@NonNull final TreeNode node) {
    final TypedQuery<TreeNode> query = _entityManager.createQuery(
      String.join(
        "", 
        "SELECT node ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE node._coordinates._start > :parentSetStart ",
        "   AND node._coordinates._end < :parentSetEnd ",
        "   AND node._coordinates._depth = :parentDepth + 1"
      ),
      _entity
    ).setParameter("parentSetStart", node.getCoordinates().getStart())
     .setParameter("parentSetEnd", node.getCoordinates().getEnd())
     .setParameter("parentDepth", node.getCoordinates().getDepth());
    
    return new HashSet<TreeNode>(query.getResultList());
  }
  
  @Override
  public NestedSetCoordinates getCoordinates () {
    return new NestedSetCoordinates(getSetStart(), getSetEnd(), getDepth());
  }

  @Override
  public NestedSetCoordinates getCoordinatesOf (@NonNull final TreeNode node) {
    final List<NestedSetCoordinates> results = _entityManager.createQuery(
      String.join(
        "",
        "SELECT node._coordinates ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE node._identifier = :identifier"
      ), NestedSetCoordinates.class
    ).setParameter("identifier", node.getIdentifier())
     .getResultList();
    
    return results.size() <= 0 ? null : results.get(0);
  }

  @Override
  public int getDepth () {
    return 0;
  }

  @Override
  public int getDepthOf (@NonNull final TreeNode node) {
    return _entityManager.createQuery(
      String.join(
        "",
        "SELECT node._coordinates._depth ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE identifier = :identifier"
      ), Integer.class
    ).setParameter("identifier", node.getIdentifier())
     .getFirstResult();
  }

  @Override
  public TreeNode getNode (@NonNull final Long identifier) {
    return _entityManager.find(_entity, identifier);
  }

  @Override
  public Set<TreeNode> getNodes () {
    final TypedQuery<TreeNode> query = _entityManager.createQuery(
      String.join("", "SELECT node FROM ", _entity.getName(), " node"),
      _entity
    );
    final Set<TreeNode> result = new HashSet<TreeNode>(query.getResultList());
    return result;
  }

  @Override
  public TreeNode getParentOf (@NonNull final TreeNode node) {    
    final TypedQuery<TreeNode> query = _entityManager.createQuery(
      String.join(
        "", 
        "SELECT node ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE node._coordinates._start < :childSetStart ",
        "   AND node._coordinates._end > :childSetEnd ",
        "   AND node._coordinates._depth = :childDepth - 1"
      ),
      _entity
    ).setParameter("childSetStart", node.getCoordinates().getStart())
     .setParameter("childSetEnd", node.getCoordinates().getEnd())
     .setParameter("childDepth", node.getCoordinates().getDepth());
    
    final List<TreeNode> result = query.getResultList();
    
    return result.size() == 0 ? null : result.get(0);
  }

  @Override
  public Set<TreeNode> getParentsOf (@NonNull final TreeNode node) {
    final TypedQuery<TreeNode> query = _entityManager.createQuery(
      String.join(
        "", 
        "SELECT node ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE node._coordinates._start < :childSetStart ",
        "   AND node._coordinates._end > :childSetEnd "
      ),
      _entity
    ).setParameter("childSetStart", node.getCoordinates().getStart())
     .setParameter("childSetEnd", node.getCoordinates().getEnd());
    
    final Set<TreeNode> result = new HashSet<TreeNode>(query.getResultList());
    
    return result;
  }

  @Override
  public int getSetEnd () {
    final TypedQuery<Integer> query = _entityManager.createQuery(
      String.join("", "SELECT MAX(node._coordinates._end) + 1 FROM ", _entity.getName(), " node"),
      Integer.class
    );
    
    final List<Integer> result = query.getResultList();
    
    return (result.size() <= 0) ? 1 : result.get(0).intValue();
  }

  @Override
  public int getSetEndOf (@NonNull final TreeNode node) {
    return _entityManager.createQuery(
      String.join(
        "",
        "SELECT node._coordinates._end ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE identifier = :identifier"
      ), Integer.class
    ).setParameter("identifier", node.getIdentifier())
     .getFirstResult();
  }

  @Override
  public int getSetStart () {
    return 0;
  }

  @Override
  public int getSetStartOf (@NonNull final TreeNode node) {    
    return _entityManager.createQuery(
      String.join(
        "",
        "SELECT node._coordinates._start ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE identifier = :identifier"
      ), Integer.class
    ).setParameter("identifier", node.getIdentifier())
     .getFirstResult();
  }

  @Override
  public void removeNode (@NonNull final TreeNode node) {    
    _entityManager.createQuery(
      String.join(
        "", 
        "UPDATE ", _entity.getName(), " ", 
        "SET _coordinates._start = _coordinates._start - :removedLength, ",
        "    _coordinates._end = _coordinates._end - :removedLength, ",
        "WHERE _coordinates._start > :removedSetEnd"
      )
    ).setParameter("removedLength", node.getCoordinates().getSize())
     .setParameter("removedSetEnd", node.getCoordinates().getEnd())
     .executeUpdate();
    
    _entityManager.createQuery(
      String.join(
        "", 
        "UPDATE ", _entity.getName(), " ", 
        "SET _coordinates._end = _coordinates._end - :removedLength, ",
        "WHERE _coordinates._end > :removedSetEnd ",
        "  AND _coordinates._start < :removedSetStart"
      )
    ).setParameter("removedLength", node.getCoordinates().getSize())
     .setParameter("removedSetEnd", node.getCoordinates().getEnd())
     .setParameter("removedSetStart", node.getCoordinates().getStart())
     .executeUpdate();
    
    _entityManager.createQuery(
      String.join(
        "", 
        "DELETE ", _entity.getName(), " node ", 
        "WHERE node._coordinates._end <= :removedSetEnd ",
        "  AND node._coordinates._start >= :removedSetStart"
      )
    ).setParameter("removedSetStart", node.getCoordinates().getStart())
     .setParameter("removedSetEnd", node.getCoordinates().getEnd())
     .executeUpdate();
  }

  @Override
  public long getSize () {    
    return _entityManager.createQuery(
      String.join("", "SELECT COUNT(node) FROM ", _entity.getName(), " node"), 
      Long.class
    ).getSingleResult().longValue();
  }

  @Override
  public TreeNode getRoot (@NonNull final TreeNode node) {
    return _entityManager.createQuery(
      String.join(
        "", 
        "SELECT node FROM ", _entity.getName(), " node ",
        " WHERE node._coordinates._end >= :childSetEnd ",
        "   AND node._coordinates._start <= :childSetStart ",
        "   AND node._coordinates._depth = 0"
      ), _entity
    ).getSingleResult();
  }
}
