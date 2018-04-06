package org.liara.api.filter.visitor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.liara.api.filter.ast.FilterNode;
import org.liara.api.filter.ast.FilterNodeType;
import org.springframework.lang.NonNull;

public class AnnotationBasedFilterASTVisitor implements FilterASTVisitor
{
  private Map<FilterNodeType, Method> _routing = new HashMap<>();
  
  public AnnotationBasedFilterASTVisitor() {
    this.computeRouting();
  }
  
  private void computeRouting () {
    final Class<? extends FilterASTVisitor> clazz = getClass();

    for (final Method method : clazz.getMethods()) {
      final VisitCommonFilterNode route = method.getAnnotation(VisitCommonFilterNode.class);
      
      if (route != null) {
        if (_routing.containsKey(route.type())) {
          throw new Error("Duplicated visitor callback for " + route.type());
        }
        
        if (method.getParameterCount() != 1) {
          throw new Error("All registered visitor callback must have only one parameter.");
        }
        
        if (FilterNode.class.isAssignableFrom(method.getParameterTypes()[0]) == false) {
          throw new Error("All registered visitor must accept a parameter that extends FilterAstNode");
        }
        
        this._routing.put(route.type(), method);
      }
    }
  }

  @Override
  public void visit (@NonNull final FilterNode node) {
    if (_routing.containsKey(node.getType())) {
      final Method method = _routing.get(node.getType());
      
      try {
        method.invoke(this, node);
      } catch (final Exception e) {
        throw new Error("Visitor callback invokation error.", e);
      }
    } else {
      visitDefault(node);
    }
  }

  public void visitDefault (@NonNull final FilterNode node) {
    
  }
}
