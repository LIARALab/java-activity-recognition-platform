package org.liara.api.filter.visitor.criteria;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import org.liara.api.criteria.CriteriaExpressionSelector;
import org.springframework.lang.NonNull;

public class CriteriaFilterASTVisitorContext<Entity>
{
  @NonNull
  private final CriteriaBuilder _builder;
  
  @NonNull
  private final CriteriaQuery<?> _query;
  
  @NonNull
  private final Path<Entity> _root;

  @NonNull
  private final Map<CriteriaExpressionSelector<?>, Expression<?>> _selections = new HashMap<>();
  
  public CriteriaFilterASTVisitorContext(
    @NonNull final CriteriaBuilder builder,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final Path<Entity> path
  ) {
    _query = query;
    _root = path;
    _builder = builder;
  }

  public CriteriaQuery<?> getCriteriaQuery() {
    return _query;
  }
  
  public CriteriaBuilder getCriteriaBuilder() {
    return _builder;
  }
  
  public Path<Entity> getRoot() {
    return _root;
  }
  
  @SuppressWarnings("unchecked")
  public <Value> Expression<Value> select(@NonNull final CriteriaExpressionSelector<Value> selector) {
   if (_selections.containsKey(selector)) {
     return (Expression<Value>) _selections.get(selector);
   } else {
     final Expression<Value> expression = selector.select(_builder, _query, _root);
     _selections.put(selector, expression);
     return expression;
   }
  }
}
