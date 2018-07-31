package org.liara.api.data.repository.local;

import java.util.Set;
import java.util.WeakHashMap;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.tree.LocalNestedSetTree;
import org.liara.api.data.entity.tree.NestedSetCoordinates;
import org.liara.api.data.entity.tree.NestedSetTree;
import org.liara.api.data.repository.NodeRepository;
import org.springframework.lang.NonNull;

public class LocalNodeRepository
       extends LocalApplicationEntityRepository<Node>
       implements NestedSetTree<Node>, NodeRepository
{
  @NonNull
  public static final WeakHashMap<NestedSetTree<Node>, LocalNodeRepository> REPOSITORIES = new WeakHashMap<>();
  
  public static LocalNodeRepository of (@NonNull final Node node) {
    return REPOSITORIES.get(node.getTree());
  }
  
  @NonNull
  private final LocalNestedSetTree<Node> _tree;
  
  public static LocalNodeRepository from (@NonNull final LocalEntityManager manager) {
    final LocalNodeRepository result = new LocalNodeRepository();
    manager.addListener(result);
    return result;
  }
  
  public LocalNodeRepository() {
    super(Node.class);
    _tree = new LocalNestedSetTree<>();
    REPOSITORIES.put(_tree, this);
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
    add(node);
  }

  @Override
  public void addNode (@NonNull final Node node, @NonNull final Node parent) {
    _tree.addNode(node, parent);
    add(node);
  }

  @Override
  public void removeNode (@NonNull final Node node) {
    _tree.removeNode(node);
    remove(node);
  }

  @Override
  public Node getRoot (@NonNull final Node node) {
    return _tree.getRoot(node);
  }

  @Override
  protected void trackedEntityWasAdded (@NonNull final Node entity) {
    super.trackedEntityWasAdded(entity);
    if (!_tree.contains(entity)) {
      _tree.addNode(entity);
    }
  }

  @Override
  protected void trackedEntityWasRemoved (@NonNull final Node entity) {
    super.trackedEntityWasRemoved(entity);
    if (_tree.contains(entity)) {
      _tree.removeNode(entity);
    }
  }
}
