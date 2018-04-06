package org.liara.api.request.parser;

import java.util.ArrayList;
import java.util.List;

import org.liara.api.collection.filtering.EntityFieldFilter;
import org.liara.api.filter.ast.DisjunctionFilterNode;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.liara.api.filter.parser.FilterParser;
import org.liara.api.filter.visitor.criteria.CriteriaFilterASTVisitor;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class APIRequestEntityFieldFilterParser<Entity, Field> implements APIRequestParser<EntityFieldFilter<Entity, Field>>
{
  @NonNull
  private final FilterParser _parser;
  
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final CriteriaFilterASTVisitor<Entity, Field> _visitor;
  
  public APIRequestEntityFieldFilterParser(
    @NonNull final String parameter, 
    @NonNull final FilterParser parser,
    @NonNull final CriteriaFilterASTVisitor<Entity, Field> visitor
  ) {
    _parser = parser;
    _parameter = parameter;
    _visitor = visitor;
  }

  @Override
  public EntityFieldFilter<Entity, Field> parse (@NonNull final APIRequest request) {
    if (request.contains(_parameter)) {
      final List<PredicateFilterNode> predicates = new ArrayList<>();
      
      for (final String value : request.getParameter(_parameter)) {
        predicates.add(_parser.parse(value));
      }
      
      return new EntityFieldFilter<>(new DisjunctionFilterNode(predicates), _visitor);
    } else {
      return null;
    }
  }

}
