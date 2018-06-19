package org.liara.api.data.entity.tree;

import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface NestedSetTreeNode<RelatedNode extends NestedSetTreeNode<RelatedNode>>
{
  public NestedSetTree<RelatedNode> getTree ();
  
  public void setTree (@Nullable final NestedSetTree<RelatedNode> tree);
  
  public Long getIdentifier ();
  
  public NestedSetCoordinates getCoordinates ();
  
  public Set<RelatedNode> getChildren ();
  
  public Set<RelatedNode> getAllChildren ();
  
  public void addChild (@NonNull final RelatedNode node);
  
  public void removeChild (@NonNull final RelatedNode node);
  
  public RelatedNode getParent ();
  
  public void setParent (@NonNull final RelatedNode node);
}
