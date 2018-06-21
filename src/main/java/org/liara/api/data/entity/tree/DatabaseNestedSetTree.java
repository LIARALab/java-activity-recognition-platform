package org.liara.api.data.entity.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

public class DatabaseNestedSetTree<TreeNode extends NestedSetTreeNode<TreeNode>> 
       implements NestedSetTree<TreeNode>
{
  @NonNull
  private final EntityManagerFactory _entityManagerFactory;
  
  @NonNull
  private final Class<TreeNode> _entity;
  
  @Autowired
  public DatabaseNestedSetTree(
    @NonNull final EntityManagerFactory entityManagerFactory,
    @NonNull final Class<TreeNode> entity
  ) {
    _entityManagerFactory = entityManagerFactory;
    _entity = entity;
  }

  @Override
  public void addNode (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    
    entityManager.persist(node);
    
    entityManager.close();
  }

  @Override
  public void addNode (@NonNull final TreeNode node, @NonNull final TreeNode parent) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    
    entityManager.createQuery(
      String.join(
        "", 
        "UPDATE ", _entity.getName(), " ", 
        "SET coordinates._start = coordinates._start + 2 ",
        "WHERE coordinates._start > :parentSetEnd"
      )
    ).setParameter("parentSetEnd", parent.getCoordinates().getEnd())
     .executeUpdate();
    
    entityManager.createQuery(
      String.join(
        "", 
        "UPDATE ", _entity.getName(), " ", 
        "SET coordinates._end = coordinates._end + 2 ",
        "WHERE coordinates._end >= :parentSetEnd"
      )
    ).setParameter("parentSetEnd", parent.getCoordinates().getEnd())
     .executeUpdate();
    
    entityManager.persist(node);
    
    entityManager.close();
  }

  @Override
  public void clear () {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    entityManager.createQuery(String.join("", "DELETE ", _entity.getName())).executeUpdate();
    entityManager.close();
  }

  @Override
  public boolean contains (@NonNull final TreeNode node) {
    if (node.getIdentifier() == null) return false;
    
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    boolean result = entityManager.find(_entity, node.getIdentifier()) != null;
    entityManager.close();
    
    return result;
  }

  @Override
  public Set<TreeNode> getAllChildrenOf (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    final TypedQuery<TreeNode> query = entityManager.createQuery(
      String.join(
        "", 
        "SELECT node",
        "  FROM ", _entity.getName(), " node ",
        " WHERE node._coordinates._start > :parentSetStart ",
        "   AND node._coordinates._end < :parentSetEnd "
      ),
      _entity
    ).setParameter("parentSetStart", node.getCoordinates().getStart())
     .setParameter("parentSetEnd", node.getCoordinates().getEnd());
    
    final Set<TreeNode> result = new HashSet<TreeNode>(query.getResultList());
    entityManager.close();
    
    return result;
  }

  @Override
  public Set<TreeNode> getChildrenOf (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    final TypedQuery<TreeNode> query = entityManager.createQuery(
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
    
    final Set<TreeNode> result = new HashSet<TreeNode>(query.getResultList());
    entityManager.close();
    
    return result;
  }
  
  @Override
  public NestedSetCoordinates getCoordinates () {
    return new NestedSetCoordinates(getSetStart(), getSetEnd(), getDepth());
  }

  @Override
  public NestedSetCoordinates getCoordinatesOf (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    
    final List<NestedSetCoordinates> results = entityManager.createQuery(
      String.join(
        "",
        "SELECT node._coordinates ",
        "  FROM ", _entity.getName(), " node",
        " WHERE identifier = :identifier"
      ), NestedSetCoordinates.class
    ).setParameter("identifier", node.getIdentifier())
     .getResultList();
    
    entityManager.close();
    
    return results.size() <= 0 ? null : results.get(0);
  }

  @Override
  public int getDepth () {
    return 0;
  }

  @Override
  public int getDepthOf (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    
    final int result = entityManager.createQuery(
      String.join(
        "",
        "SELECT node._coordinates._depth ",
        "  FROM ", _entity.getName(), " node",
        " WHERE identifier = :identifier"
      ), Integer.class
    ).setParameter("identifier", node.getIdentifier())
     .getFirstResult();
    
    entityManager.close();
    
    return result;
  }

  @Override
  public TreeNode getNode (@NonNull final Long identifier) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    final TreeNode result = entityManager.find(_entity, identifier);
    entityManager.close();
    return result;
  }

  @Override
  public Set<TreeNode> getNodes () {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    final TypedQuery<TreeNode> query = entityManager.createQuery(
      String.join("", "SELECT node FROM ", _entity.getName(), " node"),
      _entity
    );
    final Set<TreeNode> result = new HashSet<TreeNode>(query.getResultList());
    entityManager.close();
    return result;
  }

  @Override
  public TreeNode getParentOf (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    final TypedQuery<TreeNode> query = entityManager.createQuery(
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
    entityManager.close();
    
    return result.size() == 0 ? null : result.get(0);
  }

  @Override
  public Set<TreeNode> getParentsOf (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    final TypedQuery<TreeNode> query = entityManager.createQuery(
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
    entityManager.close();
    
    return result;
  }

  @Override
  public int getSetEnd () {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    final TypedQuery<Integer> query = entityManager.createQuery(
      String.join("", "SELECT MAX(node._coordinates._end) + 1 FROM ", _entity.getName(), " node"),
      Integer.class
    );
    
    final int result = (query.getFirstResult() == 0) ? 1 : query.getSingleResult().intValue();
    entityManager.close();
    
    return result;
  }

  @Override
  public int getSetEndOf (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    
    final int result = entityManager.createQuery(
      String.join(
        "",
        "SELECT node._coordinates._end ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE identifier = :identifier"
      ), Integer.class
    ).setParameter("identifier", node.getIdentifier())
     .getFirstResult();
    
    entityManager.close();
    
    return result;
  }

  @Override
  public int getSetStart () {
    return 0;
  }

  @Override
  public int getSetStartOf (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    
    final int result = entityManager.createQuery(
      String.join(
        "",
        "SELECT node._coordinates._start ",
        "  FROM ", _entity.getName(), " node ",
        " WHERE identifier = :identifier"
      ), Integer.class
    ).setParameter("identifier", node.getIdentifier())
     .getFirstResult();
    
    entityManager.close();
    
    return result;
  }

  @Override
  public void removeNode (@NonNull final TreeNode node) {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    
    entityManager.createQuery(
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
    
    entityManager.createQuery(
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
    
    entityManager.createQuery(
      String.join(
        "", 
        "DELETE ", _entity.getName(), " node ", 
        "WHERE node._coordinates._end <= :removedSetEnd ",
        "  AND node._coordinates._start >= :removedSetStart"
      )
    ).setParameter("removedSetStart", node.getCoordinates().getStart())
     .setParameter("removedSetEnd", node.getCoordinates().getEnd())
     .executeUpdate();
    
    entityManager.close();
  }

  @Override
  public long getSize () {
    final EntityManager entityManager = _entityManagerFactory.createEntityManager();
    final long result = entityManager.createQuery(
      String.join("", "SELECT COUNT(node) FROM ", _entity.getName(), " node"), 
      Long.class
    ).getSingleResult().longValue();
    
    entityManager.close();
    return result;
  }
}
