package org.liara.api.data.entity.tree;

import java.util.Set;

import org.springframework.lang.NonNull;

public interface NestedSetTree<TreeNode extends NestedSetTreeNode<TreeNode>>
{
  public long getSize ();
  
  public TreeNode getNode (@NonNull final Long identifier);
  
  public Set<TreeNode> getNodes ();
  
  public Set<TreeNode> getChildrenOf (@NonNull final TreeNode node);
  
  public Set<TreeNode> getAllChildrenOf (@NonNull final TreeNode node);
  
  public TreeNode getParentOf (@NonNull final TreeNode node);
  
  public Set<TreeNode> getParentsOf (@NonNull final TreeNode node);
  
  public NestedSetCoordinates getCoordinates ();
  
  public NestedSetCoordinates getCoordinatesOf(@NonNull final TreeNode node);
  
  public int getSetStart ();
  
  public int getSetStartOf (@NonNull final TreeNode node);
  
  public int getSetEnd ();
  
  public int getSetEndOf (@NonNull final TreeNode node);
  
  public int getDepth ();
  
  public int getDepthOf (@NonNull final TreeNode node);
  
  public void addNode (@NonNull final TreeNode node);
  
  public void addNode (
    @NonNull final TreeNode node, 
    @NonNull final TreeNode parent
  );
  
  public boolean contains (@NonNull final TreeNode node);
  
  public void removeNode (@NonNull final TreeNode node);
  
  public void clear ();
}
