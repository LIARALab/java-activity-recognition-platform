package org.liara.api.filter.interpretor;

import java.time.ZonedDateTime;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.filter.parser.DateTimeFilterParser;
import org.liara.api.filter.visitor.collection.EntityCollectionDateTimeInRangeFilterVisitor;
import org.springframework.lang.NonNull;

public class DatetimeInRangeFilterInterpretor<Entity> extends BaseFilterInterpretor<Entity, ZonedDateTime>
{
  public DatetimeInRangeFilterInterpretor (
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> start,
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> end
  ) {
    super(
      new DateTimeFilterParser(),
      new EntityCollectionDateTimeInRangeFilterVisitor<>(start, end)
    );
  }
}
