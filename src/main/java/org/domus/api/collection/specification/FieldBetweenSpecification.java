package org.domus.api.collection.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.lang.NonNull;

public class FieldBetweenSpecification<Entity, Field extends Comparable<? super Field>>
  extends FieldSpecification<Entity, Field>
{
  @NonNull
  private final Field _start;

  @NonNull
  private final Field _end;

  public FieldBetweenSpecification(
    @NonNull final SingularAttribute<Entity, Field> field,
    @NonNull final Field start,
    @NonNull final Field end
  )
  {
    super(field);
    this._start = start;
    this._end = end;
  }

  @Override
  public Predicate build (
    @NonNull final Root<Entity> root,
    @NonNull final CriteriaQuery<?> value,
    @NonNull final CriteriaBuilder builder
  )
  {
    return builder.between(root.get(this.getField()), this.getStart(), this.getEnd());
  }

  public Field getStart () {
    return this._start;
  }

  public Field getEnd () {
    return this._end;
  }
}
