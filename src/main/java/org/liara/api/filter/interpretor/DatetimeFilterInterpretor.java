package org.liara.api.filter.interpretor;

import java.time.ZonedDateTime;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.filter.parser.DateTimeFilterParser;
import org.liara.api.filter.visitor.collection.EntityCollectionDateTimeFilterVisitor;
import org.springframework.lang.NonNull;

public class DatetimeFilterInterpretor<Entity> extends BaseFilterInterpretor<Entity, ZonedDateTime>
{
  public DatetimeFilterInterpretor (
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> field
  ) {
    super(
      new DateTimeFilterParser(),
      new EntityCollectionDateTimeFilterVisitor<>(field)
    );
  }
}
