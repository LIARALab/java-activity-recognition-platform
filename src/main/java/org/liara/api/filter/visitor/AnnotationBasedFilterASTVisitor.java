/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
