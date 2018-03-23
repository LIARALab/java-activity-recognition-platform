package org.domus.api.collection.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.lang.NonNull;

public class FieldLessThanSpecification<Entity, Field extends Comparable<? super Field>>
  extends FieldSpecification<Entity, Field>
{
  @NonNull
  private final Field _value;

  public FieldLessThanSpecification(@NonNull final SingularAttribute<Entity, Field> field, @NonNull final Field value) {
    super(field);
    this._value = value;
  }

  @Override
  public Predicate build (
    @NonNull final Root<Entity> root,
    @NonNull final CriteriaQuery<?> value,
    @NonNull final CriteriaBuilder builder
  )
  {
    return builder.lessThan(root.get(this.getField()), this.getValue());
  }

  public Field getValue () {
    return this._value;
  }
}
