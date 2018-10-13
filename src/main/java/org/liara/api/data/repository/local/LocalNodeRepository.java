package org.liara.api.data.repository.local;

import org.liara.api.data.entity.Node;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.tree.LocalNestedSetRepository;
import org.liara.api.data.tree.NestedSetCoordinates;
import org.liara.api.data.tree.NestedSetRepository;
import org.springframework.lang.NonNull;

import java.util.Set;
import java.util.WeakHashMap;

public class LocalNodeRepository
       extends LocalApplicationEntityRepository<Node>
  implements NestedSetRepository<Node>,
             NodeRepository
{
  @NonNull
  public static final WeakHashMap<NestedSetRepository<Node>, LocalNodeRepository> REPOSITORIES = new WeakHashMap<>();
  
  public static LocalNodeRepository of (@NonNull final Node node) {
    return REPOSITORIES.get(node.getTree());
  }
  
  @NonNull
  private final LocalNestedSetRepository<Node> _tree;
  
  public static LocalNodeRepository from (@NonNull final LocalEntityManager manager) {
    final LocalNodeRepository result = new LocalNodeRepository();
    manager.addListener(result);
    return result;
  }
  
  public LocalNodeRepository() {
    super(Node.class);
    _tree = new LocalNestedSetRepository<>();
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
  public void addChild (@NonNull final Node node) {
    _tree.addChild(node);
    add(node);
  }

  @Override
  public void addChild (@NonNull final Node node, @NonNull final Node parent) {
    _tree.addChild(node, parent);
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
      _tree.addChild(entity);
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
