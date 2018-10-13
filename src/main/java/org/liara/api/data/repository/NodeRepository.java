package org.liara.api.data.repository;

import org.liara.api.data.entity.Node;
import org.liara.api.data.tree.NestedSetRepository;

public interface NodeRepository
  extends NestedSetRepository,
          ApplicationEntityRepository<Node>
{

}
