package org.liara.api.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import org.springframework.lang.NonNull;

@FunctionalInterface
public interface CriteriaExpressionSelector<Value>
{
  public static <Value> CriteriaExpressionSelector<Value> column (@NonNull final SimplifiedCriteriaExpressionSelector<Value> selector) {
    return (CriteriaExpressionSelector<Value>) selector;
  }
  
  public Expression<Value> select (
    @NonNull final CriteriaBuilder builder,
    @NonNull final CriteriaQuery<?> query,
    @NonNull final Path<?> _root
  );
}
