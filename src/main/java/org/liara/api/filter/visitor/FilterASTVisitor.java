package org.liara.api.filter.visitor;

import org.liara.api.filter.ast.FilterNode;
import org.springframework.lang.NonNull;

public interface FilterASTVisitor
{
  public void visit(@NonNull final FilterNode node);
}
