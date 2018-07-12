package org.liara.api.test.builder.node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.tree.LocalNestedSetTree;
import org.springframework.lang.NonNull;

public abstract class CompositeNodeBuilder<Self extends CompositeNodeBuilder<Self>> 
                extends BaseNodeBuilder<Self>
{
  @NonNull
  private final Set<BaseNodeBuilder<?>> _children = new HashSet<>();
  
  public <SubBuilder extends BaseNodeBuilder<?>> SubBuilder with (@NonNull final SubBuilder builder) {
    _children.add(builder);
    return builder;
  }
  
  public Set<BaseNodeBuilder<?>> getChildren () {
    return Collections.unmodifiableSet(_children);
  }

  @Override
  public Node build (@NonNull final LocalNestedSetTree<Node> tree) {
    final Node result = super.build(tree);
    
    for (final BaseNodeBuilder<?> child : _children) {
      result.addChild(child.build(tree));
    }
    
    return result;
  }
}
