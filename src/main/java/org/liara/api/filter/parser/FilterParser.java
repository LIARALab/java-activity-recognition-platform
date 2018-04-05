package org.liara.api.filter.parser;

import org.liara.api.filter.ast.PredicateFilterNode;
import org.springframework.lang.NonNull;

public interface FilterParser
{
  public PredicateFilterNode parse (@NonNull final String filter);
}
