package org.domus.api.executor.specification;

import org.domus.api.collection.specification.Specification;
import org.domus.api.executor.Executor;

public interface SpecificationBuilder<Entity> extends Executor
{
  public Specification<Entity> getResult ();
}
