package org.liara.api.filter.ast;

import org.liara.api.filter.visitor.FilterASTVisitor;
import org.springframework.lang.NonNull;

public class BaseFilterNode implements FilterNode
{
  @NonNull
  private final FilterNodeType _type;
  
  public BaseFilterNode (@NonNull final FilterNodeType type) {
    _type = type;
  }
  
  @Override
  public FilterNodeType getType () {
    return _type;
  }

  @Override
  public void invit (@NonNull final FilterASTVisitor visitor) {
    visitor.visit(this);
  }
}
