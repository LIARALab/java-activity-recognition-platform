package org.liara.api.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.springframework.lang.NonNull;

@FunctionalInterface
public interface CriteriaExpressionSelector<Value>
{
  public static <Value> CriteriaExpressionSelector<Value> column (@NonNull final CriteriaRootExpressionSelector<Value> selector) {
    return (CriteriaExpressionSelector<Value>) selector;
  }
  
  public Expression<Value> select (
    @NonNull final CriteriaBuilder builder,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final Root<?> root
  );
}