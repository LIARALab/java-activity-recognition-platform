package org.liara.api.data.entity.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class LocalNestedSetTreeNodeReference<TreeNode extends NestedSetTreeNode<TreeNode>>
{
  @Nullable 
  private LocalNestedSetTreeNodeReference<TreeNode> _parent;
  
  @NonNull 
  private final NestedSetCoordinates _coordinates = new NestedSetCoordinates();
  
  @NonNull 
  private final TreeNode _node;
  
  @NonNull 
  private final Set<LocalNestedSetTreeNodeReference<TreeNode>> _children;
  
  public LocalNestedSetTreeNodeReference (@NonNull final TreeNode node) {
    _node = node;
    _children = new HashSet<>();
    _parent = null;
  }
  
  public NestedSetCoordinates getCoordinates () {
    return _coordinates;
  }
  
  public TreeNode getNode () {
    return _node;
  }
  
  public LocalNestedSetTreeNodeReference<TreeNode> getParent () {
    return _parent;
  }
  
  public boolean hasParent () {
    return _parent != null;
  }
  
  public TreeNode getParentNode () {
    return (_parent == null) ? null : _parent.getNode();
  }
  
  public void moveRight (final int move) {
    for (LocalNestedSetTreeNodeReference<TreeNode> child : getAllChildren()) {
      child.getCoordinates().moveRight(2);
      
      for (LocalNestedSetTreeNodeReference<TreeNode> parent : getAllParents()) {
        parent.getCoordinates().setEnd(parent.getCoordinates().getEnd() + 2);
      }
    }
  }
  
  public void moveLeft (final int move) {
    for (LocalNestedSetTreeNodeReference<TreeNode> child : getAllChildren()) {
      child.getCoordinates().moveLeft(2);
      
      for (LocalNestedSetTreeNodeReference<TreeNode> parent : getAllParents()) {
        parent.getCoordinates().setEnd(parent.getCoordinates().getEnd() - 2);
      }
    }
  }
  
  public void addChild (
    @NonNull final LocalNestedSetTreeNodeReference<TreeNode> node
  ) {
    if (!_children.contains(node)) {
      node.getCoordinates().set(
        _coordinates.getEnd(),
        _coordinates.getEnd() + 1, 
        _coordinates.getDepth() + 1
      );

      _coordinates.setEnd(_coordinates.getEnd() + 2);
      
      for (LocalNestedSetTreeNodeReference<TreeNode> parent : getAllParents()) {
        parent.getCoordinates().setEnd(parent.getCoordinates().getEnd() + 2);
      }
      
      _children.add(node);
      node.setParent(this);
    }
  }
  
  public void removeChild (@NonNull final LocalNestedSetTreeNodeReference<TreeNode> node) {
    if (_children.contains(node)) {
      node.getCoordinates().setDefault();

      _coordinates.setEnd(_coordinates.getEnd() - 2);
      
      for (LocalNestedSetTreeNodeReference<TreeNode> parent : getAllParents()) {
        parent.getCoordinates().setEnd(parent.getCoordinates().getEnd() - 2);
      }
      
      _children.remove(node);
      node.setParent(null);
    }
  }
  
  public void setParent (@NonNull final LocalNestedSetTreeNodeReference<TreeNode> parent) {
    if (!Objects.equals(_parent, parent)) {
      if (!Objects.equals(_parent, null)) {
        final LocalNestedSetTreeNodeReference<TreeNode> oldParent = _parent;
        _parent = null;
        oldParent.removeChild(this);
      }
      
      _parent = parent;
      
      if (!Objects.equals(_parent, null)) {
        _parent.addChild(this);
      }
    }
  }
  
  public Set<LocalNestedSetTreeNodeReference<TreeNode>> getAllParents () {
    final Set<LocalNestedSetTreeNodeReference<TreeNode>> result = new HashSet<>();
    LocalNestedSetTreeNodeReference<TreeNode> previous = this;
    
    while (previous.hasParent()) {
      previous = previous.getParent();
      result.add(previous);
    }
    
    return result;
  }
  
  public Set<TreeNode> getAllParentNodes () {
    final Set<TreeNode> result = new HashSet<>();
    LocalNestedSetTreeNodeReference<TreeNode> previous = this;
    
    while (previous.hasParent()) {
      previous = previous.getParent();
      result.add(previous.getNode());
    }
    
    return result;
  }

  
  public LocalNestedSetTreeNodeReference<TreeNode> getRoot () {
    LocalNestedSetTreeNodeReference<TreeNode> result = this;
    
    while (result.hasParent()) result = result.getParent();
    
    return result;
  }
  
  public TreeNode getRootNode () {
    return getRoot().getNode();
  }
  
  public Set<LocalNestedSetTreeNodeReference<TreeNode>> getChildren () {
    return _children;
  }
  
  public Set<TreeNode> getChildrenNodes () {
    return _children.stream()
                    .map(LocalNestedSetTreeNodeReference::getNode)
                    .collect(Collectors.toSet());
  }
  
  public Set<TreeNode> getAllChildrenNodes () {
    final List<
      Iterator<
        LocalNestedSetTreeNodeReference<TreeNode>
      >
    > stack = new ArrayList<>();
    final Set<TreeNode> result = new HashSet<>();
    
    stack.add(_children.iterator());
    
    while (stack.size() > 0) {
      final Iterator<
        LocalNestedSetTreeNodeReference<TreeNode>
      > iterator = stack.get(stack.size() - 1);
      
      if (iterator.hasNext()) {
        final LocalNestedSetTreeNodeReference<TreeNode> nextReference = iterator.next();
        result.add(nextReference.getNode());
        stack.add(nextReference.getChildren().iterator());
      } else {
        stack.remove(stack.size() - 1);
      }
    }
    
    return result;
  }
  
  public Set<LocalNestedSetTreeNodeReference<TreeNode>> getAllChildren () {
    final List<
      Iterator<
        LocalNestedSetTreeNodeReference<TreeNode>
      >
    > stack = new ArrayList<>();
    final Set<LocalNestedSetTreeNodeReference<TreeNode>> result = new HashSet<>();
    
    stack.add(_children.iterator());
    
    while (stack.size() > 0) {
      final Iterator<
        LocalNestedSetTreeNodeReference<TreeNode>
      > iterator = stack.get(stack.size() - 1);
      
      if (iterator.hasNext()) {
        final LocalNestedSetTreeNodeReference<TreeNode> nextReference = iterator.next();
        result.add(nextReference);
        stack.add(nextReference.getChildren().iterator());
      } else {
        stack.remove(stack.size() - 1);
      }
    }
    
    return result;
  }
}
