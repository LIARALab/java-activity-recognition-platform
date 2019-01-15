package org.liara.api.data.tree;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class LocalNestedSetTree
{
  @NonNull
  private final TreeSet<LocalNestedSetTreeNodeReference<NestedSet>> _roots;

  @NonNull
  private final BiMap<LocalNestedSetTreeNodeReference<NestedSet>, NestedSet> _nodesByReference;

  @NonNull
  private final BiMap<Long, NestedSet> _nodeByIdentifier;

  public LocalNestedSetTree () {
    _roots = new TreeSet<>(LocalNestedSetTreeNodeReferenceComparator.getInstance());
    _nodesByReference = HashBiMap.create();
    _nodeByIdentifier = HashBiMap.create();
  }

  public @NonNull NestedSet getNode (@NonNull final Long identifier) {
    return _nodeByIdentifier.get(identifier);
  }

  public @NonNull Set<@NonNull NestedSet> getNodes () {
    return Collections.unmodifiableSet(_nodesByReference.inverse().keySet());
  }

  public @NonNull List<@NonNull NestedSet> getChildrenOf (
    @NonNull final NestedSet node
  )
  {
    return (List<NestedSet>) _nodesByReference.inverse().get(node).getChildrenNodes();
  }

  public @NonNull List<@NonNull NestedSet> getAllChildrenOf (@NonNull final NestedSet node) {
    return (List<NestedSet>) _nodesByReference.inverse().get(node).getAllChildrenNodes();
  }

  public NestedSet getParentOf (@NonNull final NestedSet node) {
    return _nodesByReference.inverse().get(node).getParentNode();
  }

  public @NonNull List<@NonNull NestedSet> getParentsOf (@NonNull final NestedSet node) {
    return (List<NestedSet>) _nodesByReference.inverse().get(node).getAllParentNodes();
  }

  public int getSetStart () {
    return 0;
  }

  public int getSetEnd () {
    if (_nodeByIdentifier.size() <= 0) return 1;
    else return _roots.last().getCoordinates().getEnd() + 1;
  }

  public void attachChild (@NonNull final NestedSet node) {
    if (!contains(node)) {
      final LocalNestedSetTreeNodeReference<NestedSet> reference = new LocalNestedSetTreeNodeReference<>(node);

      reference.getCoordinates().set(getSetEnd(), getSetEnd() + 1, 1);

      _roots.add(reference);
      _nodeByIdentifier.put(node.getIdentifier(), node);
      _nodesByReference.put(reference, node);
    }
  }

  public void attachChild (
    @NonNull final NestedSet node, @NonNull final NestedSet parent
  )
  {
    if (Objects.equals(parent, null) && (!contains(node) || getParentOf(node) != null)) {
      if (contains(node)) removeNode(node);
      attachChild(node);
    } else if (!contains(node) || !Objects.equals(getParentOf(node), parent)) {
      final LocalNestedSetTreeNodeReference<NestedSet> childReference;

      if (contains(node)) {
        childReference = _nodesByReference.inverse().get(node);
        removeNode(node);
      } else {
        childReference = new LocalNestedSetTreeNodeReference<>(node);
      }

      final LocalNestedSetTreeNodeReference<NestedSet> parentReference = _nodesByReference.inverse().get(parent);

      parentReference.addChild(childReference);

      for (LocalNestedSetTreeNodeReference<NestedSet> reference : _roots.subSet(
        parentReference.getRoot(),
        false,
        _roots.last(),
        true
      )) {
        reference.moveRight(2);
      }

      _nodesByReference.put(childReference, node);
      _nodeByIdentifier.put(node.getIdentifier(), node);
    }
  }

  public boolean contains (@NonNull final NestedSet node) {
    return _nodeByIdentifier.containsValue(node);
  }

  public void removeNode (@NonNull final NestedSet node) {
    if (contains(node)) {
      final LocalNestedSetTreeNodeReference<NestedSet> reference = _nodesByReference.inverse().get(node);

      if (reference.hasParent()) {
        final LocalNestedSetTreeNodeReference<NestedSet> updatedRoot = reference.getRoot();
        reference.setParent(null);
        for (LocalNestedSetTreeNodeReference<NestedSet> root : _roots.subSet(updatedRoot, false, _roots.last(), true)) {
          root.moveLeft(2);
        }
      } else {
        for (LocalNestedSetTreeNodeReference<NestedSet> root : _roots.subSet(reference, false, _roots.last(), true)) {
          root.moveLeft(2);
        }

        _roots.remove(reference);
      }

      _nodesByReference.remove(reference);
      _nodeByIdentifier.remove(node.getIdentifier());
    }
  }

  public void clear () {
    _nodeByIdentifier.clear();
    _nodesByReference.clear();
  }

  public NestedSetCoordinates getCoordinates () {
    return new NestedSetCoordinates(getSetStart(), getSetEnd(), getDepth());
  }

  public NestedSetCoordinates getCoordinatesOf (@NonNull final NestedSet node) {
    return new NestedSetCoordinates(_nodesByReference.inverse().get(node).getCoordinates());
  }

  public int getDepth () {
    return 0;
  }

  public NestedSet getRoot (@NonNull final NestedSet node) {
    NestedSet result = node;

    while (getParentOf(result) != null) {
      result = getParentOf(result);
    }

    return result;
  }
}
