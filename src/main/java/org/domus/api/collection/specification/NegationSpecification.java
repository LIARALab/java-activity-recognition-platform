package org.domus.api.collection.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.lang.NonNull;

public class NegationSpecification<Entity> implements Specification<Entity>
{
  @NonNull
  private final Specification<Entity> _predicate;

  public NegationSpecification(@NonNull final Specification<Entity> predicate) {
    this._predicate = predicate;
  }

  @Override
  public Predicate build (
    @NonNull final Root<Entity> root,
    @NonNull final CriteriaQuery<?> value,
    @NonNull final CriteriaBuilder builder
  )
  {
    return builder.not(this._predicate.build(root, value, builder));
  }

  public Specification<Entity> getPredicate () {
    return this._predicate;
  }
}
