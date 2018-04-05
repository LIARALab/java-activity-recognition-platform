package org.liara.api.filter.visitor.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;

import org.springframework.lang.NonNull;

public class CriteriaFilterASTVisitorContext<Entity, Filtered>
{
  @NonNull
  private final CriteriaBuilder _builder;
  
  @NonNull
  private final CriteriaQuery<?> _query;

  @NonNull
  private final Expression<Filtered> _filtered;
  
  public CriteriaFilterASTVisitorContext(
    @NonNull final CriteriaBuilder _builder,
    @NonNull final CriteriaQuery<?> _query,
    @NonNull final Expression<Filtered> _filtered
  ) {
    this._query = _query;
    this._builder = _builder;
    this._filtered = _filtered;
  }

  public CriteriaQuery<?> getCriteriaQuery() {
    return _query;
  }
  
  public CriteriaBuilder getCriteriaBuilder() {
    return _builder;
  }
  
  public Expression<Filtered> getFiltered() {
    return _filtered;
  }
}
