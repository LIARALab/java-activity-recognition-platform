package org.domus.api.collection.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.lang.NonNull;

public abstract class FieldSpecification<Entity, Field> implements Specification<Entity>
{
  @NonNull
  private final SingularAttribute<Entity, Field> _field;

  public FieldSpecification(@NonNull final SingularAttribute<Entity, Field> field) {
    this._field = field;
  }

  public abstract Predicate build (
    @NonNull final Root<Entity> root,
    @NonNull final CriteriaQuery<?> value,
    @NonNull final CriteriaBuilder builder
  );

  public SingularAttribute<Entity, Field> getField () {
    return this._field;
  }
}
