package org.liara.api.data.entity.tree;

import java.util.Comparator;

import org.springframework.lang.NonNull;

public class LocalNestedSetTreeNodeReferenceComparator 
       implements Comparator<LocalNestedSetTreeNodeReference<?>>
{
  private static LocalNestedSetTreeNodeReferenceComparator INSTANCE = null;
  
  public static LocalNestedSetTreeNodeReferenceComparator getInstance () {
    if (LocalNestedSetTreeNodeReferenceComparator.INSTANCE == null) {
      LocalNestedSetTreeNodeReferenceComparator.INSTANCE = new LocalNestedSetTreeNodeReferenceComparator();
    }
    
    return LocalNestedSetTreeNodeReferenceComparator.INSTANCE;
  }

  @Override
  public int compare (
    @NonNull final LocalNestedSetTreeNodeReference<?> left, 
    @NonNull final LocalNestedSetTreeNodeReference<?> right
  ) {
    return left.getCoordinates().getStart() - right.getCoordinates().getStart();
  }
}
