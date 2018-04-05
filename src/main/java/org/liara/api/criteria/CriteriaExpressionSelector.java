package org.liara.api.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.springframework.lang.NonNull;

@FunctionalInterface
public interface CriteriaExpressionSelector<Value>
{
  public Expression<Value> select (
    @NonNull final CriteriaBuilder builder,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final Root<?> root
  );
}
