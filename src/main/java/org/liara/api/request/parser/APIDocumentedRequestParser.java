package org.liara.api.request.parser;

import java.util.Collections;
import java.util.List;

import org.springframework.lang.NonNull;

import springfox.documentation.service.Parameter;

public interface APIDocumentedRequestParser
{  
  public default String getName () {
    return null;
  }
  
  public default String getFullName (@NonNull final List<APIDocumentedRequestParser> parents) {
    final StringBuilder builder = new StringBuilder();
    for (final APIDocumentedRequestParser parent : parents) {
      final String parentName = parent.getName();
      if (parentName != null) {
        if (builder.length() > 0) builder.append('.');
        builder.append(parentName);
      }
    }
    
    return builder.toString();
  }
  
  public List<Parameter> getHandledParametersDocumentation (@NonNull final List<APIDocumentedRequestParser> parents);
  
  public default List<Parameter> getHandledParametersDocumentation () {
    return getHandledParametersDocumentation(Collections.emptyList());
  }
}
