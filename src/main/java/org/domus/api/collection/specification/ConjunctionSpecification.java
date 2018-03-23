package org.domus.api.collection.specification;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.lang.NonNull;

import com.google.common.collect.Lists;

public class ConjunctionSpecification<Entity> implements Specification<Entity>
{
  @NonNull
  private final List<Specification<Entity>> _predicates;

  @NonNull
  private final List<Specification<Entity>> _readOnlyPredicates;

  public ConjunctionSpecification(@NonNull final Iterable<Specification<Entity>> predicates) {
    this._predicates = Lists.newArrayList(predicates);
    this._readOnlyPredicates = Collections.unmodifiableList(this._predicates);
  }

  public ConjunctionSpecification(@NonNull final Iterator<Specification<Entity>> predicates) {
    this._predicates = Lists.newArrayList(predicates);
    this._readOnlyPredicates = Collections.unmodifiableList(this._predicates);
  }

  public ConjunctionSpecification(@NonNull final Specification<Entity>[] predicates) {
    this._predicates = Lists.newArrayList(predicates);
    this._readOnlyPredicates = Collections.unmodifiableList(this._predicates);
  }

  @Override
  public Predicate build (
    @NonNull final Root<Entity> root,
    @NonNull final CriteriaQuery<?> value,
    @NonNull final CriteriaBuilder builder
  )
  {
    final Predicate[] predicates = this._predicates.stream().map(specBuilder -> specBuilder.build(root, value, builder))
      .toArray(size -> new Predicate[size]);

    if (predicates.length <= 0) return builder.conjunction();
    else return builder.and(predicates);
  }

  public List<Specification<Entity>> getPredicates () {
    return this._readOnlyPredicates;
  }
}
