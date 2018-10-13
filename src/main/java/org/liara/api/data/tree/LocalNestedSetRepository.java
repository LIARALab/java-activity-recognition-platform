package org.liara.api.data.tree;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.List;

public class LocalNestedSetRepository
  implements NestedSetRepository
{
  @NonNull
  private final HashMap<Class<?>, LocalNestedSetTree> _trees;

  public LocalNestedSetRepository () {
    _trees = new HashMap<>();
  }

  private @NonNull LocalNestedSetTree getTree (@NonNull final Class<?> type) {
    if (!_trees.containsKey(type)) {
      _trees.put(type, new LocalNestedSetTree());
    }

    return _trees.get(type);
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getChildrenOf (@NonNull final Node node) {
    return (List<Node>) getTree(node.getClass()).getChildrenOf(node);
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getAllChildrenOf (@NonNull final Node node) {
    return (List<Node>) getTree(node.getClass()).getAllChildrenOf(node);
  }

  @Override
  public <Node extends NestedSet> @Nullable Node getParentOf (@NonNull final Node node) {
    return (Node) getTree(node.getClass()).getParentOf(node);
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getParentsOf (@NonNull final Node node) {
    return (List<Node>) getTree(node.getClass()).getParentsOf(node);
  }

  @Override
  public void attachChild (@NonNull final NestedSet node) {
    getTree(node.getClass()).attachChild(node);
  }

  @Override
  public void attachChild (
    @NonNull NestedSet node, @Nullable NestedSet parent
  )
  {

  }

  @Override
  public void removeChild (@NonNull NestedSet node) {

  }

  @Override
  public <Node extends NestedSet> Node getRoot (@NonNull Node node) {
    return null;
  }
}
