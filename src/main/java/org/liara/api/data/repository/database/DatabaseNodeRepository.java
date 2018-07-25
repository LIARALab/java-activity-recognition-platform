package org.liara.api.data.repository.database;

import java.util.Set;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.tree.DatabaseNestedSetTree;
import org.liara.api.data.entity.tree.NestedSetCoordinates;
import org.liara.api.data.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Primary
public class DatabaseNodeRepository
       extends DatabaseApplicationEntityRepository<Node>
       implements NodeRepository
{
  @NonNull
  private final EntityManager _entityManager;
  
  @NonNull
  private final DatabaseNestedSetTree<Node> _tree;
  
  @Autowired
  public DatabaseNodeRepository(
    @NonNull final EntityManager entityManager
  ) {
    super(entityManager, Node.class);
    _entityManager = entityManager;
    _tree = new DatabaseNestedSetTree<>(entityManager, Node.class);
  }

  @Override
  public long getSize () {
    return _tree.getSize();
  }

  @Override
  public Node getNode (@NonNull final Long identifier) {
    return _tree.getNode(identifier);
  }

  @Override
  public Set<Node> getNodes () {
    return _tree.getNodes();
  }

  @Override
  public Set<Node> getChildrenOf (@NonNull final Node node) {
    return _tree.getChildrenOf(node);
  }

  @Override
  public Set<Node> getAllChildrenOf (@NonNull final Node node) {
    return _tree.getAllChildrenOf(node);
  }

  @Override
  public Node getParentOf (@NonNull final Node node) {
    return _tree.getParentOf(node);
  }

  @Override
  public Set<Node> getParentsOf (@NonNull final Node node) {
    return _tree.getParentsOf(node);
  }

  @Override
  public NestedSetCoordinates getCoordinates () {
    return _tree.getCoordinates();
  }

  @Override
  public NestedSetCoordinates getCoordinatesOf (@NonNull final Node node) {
    return _tree.getCoordinatesOf(node);
  }

  @Override
  public int getSetStart () {
    return _tree.getSetStart();
  }

  @Override
  public int getSetStartOf (@NonNull final Node node) {
    return _tree.getSetStartOf(node);
  }

  @Override
  public int getSetEnd () {
    return _tree.getSetEnd();
  }

  @Override
  public int getSetEndOf (@NonNull final Node node) {
    return _tree.getSetEndOf(node);
  }

  @Override
  public int getDepth () {
    return _tree.getDepth();
  }

  @Override
  public int getDepthOf (@NonNull final Node node) {
    return _tree.getDepthOf(node);
  }

  @Override
  public void addNode (@NonNull final Node node) {
    _tree.addNode(node);
  }

  @Override
  public void addNode (@NonNull final Node node, @Nullable final Node parent) {
    _tree.addNode(node, parent);
  }

  @Override
  public boolean contains (@NonNull final Node node) {
    return _tree.contains(node);
  }

  @Override
  public void removeNode (@NonNull final Node node) {
    _tree.removeNode(node);
  }

  @Override
  public void clear () {
    _tree.clear();
  }

  @Override
  public Node getRoot (@NonNull final Node node) {
    return _tree.getRoot(node);
  }
}
