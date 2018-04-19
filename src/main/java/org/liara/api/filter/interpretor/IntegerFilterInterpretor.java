package org.liara.api.filter.interpretor;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.filter.parser.IntegerFilterParser;
import org.liara.api.filter.visitor.collection.EntityCollectionComparableFilterVisitor;
import org.springframework.lang.NonNull;

public class IntegerFilterInterpretor<Entity> extends BaseFilterInterpretor<Entity, Integer>
{
  public IntegerFilterInterpretor (
    @NonNull final EntityFieldSelector<Entity, Expression<Integer>> field
  ) {
    super(
      new IntegerFilterParser(),
      new EntityCollectionComparableFilterVisitor<>(field)
    );
  }
}
