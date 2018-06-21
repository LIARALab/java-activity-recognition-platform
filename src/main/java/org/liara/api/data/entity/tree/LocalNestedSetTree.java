package org.liara.api.data.entity.tree;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.lang.NonNull;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class LocalNestedSetTree<TreeNode extends NestedSetTreeNode<TreeNode>>
       implements NestedSetTree<TreeNode>
{
  @NonNull
  private final TreeSet<LocalNestedSetTreeNodeReference<TreeNode>> _roots;
  
  @NonNull
  private final BiMap<
    LocalNestedSetTreeNodeReference<TreeNode>, 
    TreeNode
  > _nodesByReference;
  
  @NonNull
  private final BiMap<Long, TreeNode> _nodeByIdentifier;
  
  public LocalNestedSetTree () {
    _roots = new TreeSet<>(LocalNestedSetTreeNodeReferenceComparator.getInstance());
    _nodesByReference = HashBiMap.create();
    _nodeByIdentifier = HashBiMap.create();
  }
  
  @Override
  public long getSize () {
    return _nodesByReference.size();
  }

  @Override
  public TreeNode getNode (@NonNull final Long identifier) {
    return _nodeByIdentifier.get(identifier);
  }

  @Override
  public Set<TreeNode> getNodes () {
    return Collections.unmodifiableSet(_nodesByReference.inverse().keySet());
  }

  @Override
  public Set<TreeNode> getChildrenOf (@NonNull final TreeNode node) {
    return _nodesByReference.inverse().get(node).getChildrenNodes();
  }

  @Override
  public Set<TreeNode> getAllChildrenOf (@NonNull final TreeNode node) {
    return _nodesByReference.inverse().get(node).getAllChildrenNodes();
  }

  @Override
  public TreeNode getParentOf (@NonNull final TreeNode node) {
    return _nodesByReference.inverse().get(node).getParentNode();
  }

  @Override
  public Set<TreeNode> getParentsOf (@NonNull final TreeNode node) {
    return _nodesByReference.inverse().get(node).getAllParentNodes();
  }

  @Override
  public int getSetStart () {
    return 0;
  }

  @Override
  public int getSetEnd () {
    if (getSize() <= 0) return 1;
    else return _roots.last().getCoordinates().getEnd() + 1;
  }

  @Override
  public void addNode (@NonNull final TreeNode node) {
    if (!this.contains(node)) {
      final LocalNestedSetTreeNodeReference<TreeNode> reference = new LocalNestedSetTreeNodeReference<>(node);
      
      reference.getCoordinates().set(getSetEnd(), getSetEnd() + 1, 1);
      
      _roots.add(reference);
      _nodeByIdentifier.put(node.getIdentifier(), node);
      _nodesByReference.put(reference, node);
      node.setTree(this);
    }
  }

  @Override
  public void addNode (
    @NonNull final TreeNode node, 
    @NonNull final TreeNode parent
  ) {
    if (Objects.equals(parent, null) && (!contains(node) || getParentOf(node) != null)) {
      if (contains(node)) removeNode(node);
      addNode(node);
    } else if (!contains(node) || !Objects.equals(getParentOf(node), parent)) {
      final LocalNestedSetTreeNodeReference<TreeNode> childReference;
        
      if (contains(node)) {
        childReference = _nodesByReference.inverse().get(node);
        removeNode(node);
      } else {
        childReference = new LocalNestedSetTreeNodeReference<>(node);
      }
      
      final LocalNestedSetTreeNodeReference<TreeNode> parentReference = _nodesByReference.inverse().get(parent);
      
      parentReference.addChild(childReference);
      
      for (
        LocalNestedSetTreeNodeReference<TreeNode> reference : _roots.subSet(parentReference.getRoot(), false, _roots.last(), true)
      ) {
        reference.moveRight(2);
      }

      _nodesByReference.put(childReference, node);
      _nodeByIdentifier.put(node.getIdentifier(), node);
      
      node.setTree(this);
    }
  }

  @Override
  public boolean contains (@NonNull final TreeNode node) {
    return _nodeByIdentifier.containsValue(node);
  }

  @Override
  public void removeNode (@NonNull final TreeNode node) {
    if (contains(node)) {
      final LocalNestedSetTreeNodeReference<TreeNode> reference = _nodesByReference.inverse().get(node);
      
      if (reference.hasParent()) {
        final LocalNestedSetTreeNodeReference<TreeNode> updatedRoot = reference.getRoot();
        reference.setParent(null);
        for (
          LocalNestedSetTreeNodeReference<TreeNode> root : _roots.subSet(updatedRoot, false, _roots.last(), true)
        ) {
          root.moveLeft(2);
        }
      } else {
        for (
          LocalNestedSetTreeNodeReference<TreeNode> root : _roots.subSet(
            reference, false, _roots.last(), true
          )
        ) {
          root.moveLeft(2);
        }
        
        _roots.remove(reference);
      }
      
      _nodesByReference.remove(reference);
      _nodeByIdentifier.remove(node.getIdentifier());
    }
  }

  @Override
  public void clear () {
    final Iterator<TreeNode> nodes = getNodes().iterator();
    
    while (nodes.hasNext()) {
      nodes.next().setTree(null);
    }
  }

  @Override
  public NestedSetCoordinates getCoordinates () {
    return new ImmutableNestedSetCoordinates(getSetStart(), getSetEnd(), getDepth());
  }

  @Override
  public NestedSetCoordinates getCoordinatesOf (@NonNull final TreeNode node) {
    return new ImmutableNestedSetCoordinates(
      _nodesByReference.inverse().get(node).getCoordinates()
    );
  }

  @Override
  public int getSetStartOf (@NonNull final TreeNode node) {
    return _nodesByReference.inverse().get(node).getCoordinates().getStart();
  }

  @Override
  public int getSetEndOf (@NonNull final TreeNode node) {
    return _nodesByReference.inverse().get(node).getCoordinates().getEnd();
  }

  @Override
  public int getDepth () {
    return 0;
  }

  @Override
  public int getDepthOf (@NonNull final TreeNode node) {
    return _nodesByReference.inverse().get(node).getCoordinates().getDepth();
  }
}
