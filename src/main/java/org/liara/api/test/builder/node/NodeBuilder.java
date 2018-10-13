package org.liara.api.test.builder.node;

import groovy.lang.Closure;
import org.liara.api.data.entity.Node;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;

public class NodeBuilder extends CompositeNodeBuilder<NodeBuilder, Node>
{
  /**
   * Configure a new house and return the resulting builder.
   * 
   * @param closure Groovy configuration closure.
   * 
   * @return The resulting builder.
   */
  public static NodeBuilder house (
    @NonNull final Closure<?> closure
  ) {
    final NodeBuilder result = new NodeBuilder();
    result.withType("/common/house");
    
    Closures.callAs(closure, result);
    
    return result;
  }
  
  /**
   * Configure a new room and return the resulting builder.
   * 
   * @param closure Groovy configuration closure.
   * 
   * @return The resulting builder.
   */
  public static NodeBuilder room (
    @NonNull final Closure<?> closure
  ) {
    final NodeBuilder result = new NodeBuilder();
    result.withType("/common/room");
    
    Closures.callAs(closure, result);
    
    return result;
  }
  
  /**
   * Configure a new furniture and return the resulting builder.
   * 
   * @param closure Groovy configuration closure.
   * 
   * @return The resulting builder.
   */
  public static NodeBuilder furniture (
    @NonNull final Closure<?> closure
  ) {
    final NodeBuilder result = new NodeBuilder();
    result.withType("/common/furniture");
    
    Closures.callAs(closure, result);
    
    return result;
  }
  
  /**
   * @see BaseNodeBuilder#self()
   */
  @Override
  public NodeBuilder self () {
    return this;
  }

  @Override
  public Node build () {
    final Node node = Node.createLocal();
    super.apply(node);
    return node;
  }
}
