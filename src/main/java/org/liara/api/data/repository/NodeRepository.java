package org.liara.api.data.repository;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.tree.NestedSetTree;

public interface NodeRepository
      extends NestedSetTree<Node>, ApplicationEntityRepository<Node>
{

}
