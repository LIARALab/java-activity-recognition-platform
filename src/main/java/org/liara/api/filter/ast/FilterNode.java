package org.liara.api.filter.ast;

import org.liara.api.filter.visitor.FilterASTVisitor;
import org.springframework.lang.NonNull;

public interface FilterNode
{
  public FilterNodeType getType();
  
  public void invit(@NonNull final FilterASTVisitor visitor);
}
