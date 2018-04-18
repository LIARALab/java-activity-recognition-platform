package org.liara.api.request.parser.filtering;

import java.util.ArrayList;
import java.util.List;

import org.liara.api.collection.operator.ASTBasedEntityFilter;
import org.liara.api.collection.operator.EntityCollectionFilteringOperator;
import org.liara.api.filter.ast.DisjunctionFilterNode;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.liara.api.filter.parser.FilterParser;
import org.liara.api.filter.visitor.collection.EntityCollectionFilterVisitor;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

public class APIRequestASTBasedEntityFilterParser<Entity, Field> implements APIRequestEntityCollectionFilteringOperatorParser<Entity>
{
  @NonNull
  private final FilterParser _parser;
  
  @NonNull
  private final String _parameter;
  
  @NonNull
  private final EntityCollectionFilterVisitor<Entity, Field> _visitor;
  
  public APIRequestASTBasedEntityFilterParser(
    @NonNull final String parameter, 
    @NonNull final FilterParser parser,
    @NonNull final EntityCollectionFilterVisitor<Entity, Field> visitor
  ) {
    _parser = parser;
    _parameter = parameter;
    _visitor = visitor;
  }

  @Override
  public EntityCollectionFilteringOperator<Entity> parse (@NonNull final APIRequest request) {
    if (request.contains(_parameter)) {
      final List<PredicateFilterNode> predicates = new ArrayList<>();
      
      for (final String value : request.getParameter(_parameter)) {
        predicates.add(_parser.parse(value));
      }
      
      return new ASTBasedEntityFilter<>(new DisjunctionFilterNode(predicates), _visitor);
    } else {
      return null;
    }
  }

}
