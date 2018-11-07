package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Node;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.tree.LocalNestedSetTree;
import org.liara.api.data.tree.NestedSet;
import org.liara.api.data.tree.NestedSetRepository;

import java.util.List;


public class LocalNodeRepository
       extends LocalApplicationEntityRepository<Node>
  implements NestedSetRepository,
             NodeRepository
{
  @NonNull
  private final LocalNestedSetTree _tree;

  public static LocalNodeRepository from (@NonNull final ApplicationEntityManager manager) {
    final LocalNodeRepository result = new LocalNodeRepository();
    manager.addListener(result);
    return result;
  }
  
  public LocalNodeRepository() {
    super(Node.class);
    _tree = new LocalNestedSetTree();
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getChildrenOf (
    @NonNull final Node node
  )
  {
    return (List<Node>) _tree.getChildrenOf(node);
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getAllChildrenOf (
    @NonNull final Node node
  )
  {
    return (List<Node>) _tree.getAllChildrenOf(node);
  }

  @Override
  public <Node extends NestedSet> @Nullable Node getParentOf (@NonNull final Node node) {
    return (Node) _tree.getParentOf(node);
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getParentsOf (
    @NonNull final Node node
  )
  {
    return (List<Node>) _tree.getParentsOf(node);
  }

  @Override
  public void attachChild (@NonNull final NestedSet node) {
    if (node instanceof Node) {
      _tree.attachChild(node);
      add((ApplicationEntity) node);
    }
  }

  @Override
  public void attachChild (
    @NonNull final NestedSet node, @Nullable final NestedSet parent
  )
  {
    if (node instanceof Node) {
      _tree.attachChild(node, parent);
      add((ApplicationEntity) node);
    }
  }

  @Override
  public void removeChild (@NonNull final NestedSet node) {
    _tree.removeNode(node);
  }

  @Override
  public <Node extends NestedSet> Node getRoot (@NonNull final Node node) {
    return (Node) _tree.getRoot(node);
  }

  @Override
  protected void trackedEntityWasAdded (@NonNull final Node entity) {
    super.trackedEntityWasAdded(entity);
    if (!_tree.contains(entity)) {
      _tree.attachChild(entity);
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
