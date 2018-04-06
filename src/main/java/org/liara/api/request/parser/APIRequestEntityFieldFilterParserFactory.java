package org.liara.api.request.parser;

import java.time.LocalDateTime;

import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.criteria.CriteriaRootExpressionSelector;
import org.liara.api.filter.parser.DateTimeFilterParser;
import org.liara.api.filter.parser.DurationFilterParser;
import org.liara.api.filter.parser.IntegerFilterParser;
import org.liara.api.filter.visitor.criteria.CriteriaDateTimeFilterASTVisitor;
import org.liara.api.filter.visitor.criteria.CriteriaDateTimeInRangeFilterASTVisitor;
import org.liara.api.filter.visitor.criteria.CriteriaComparableFilterASTVisitor;
import org.springframework.lang.NonNull;

public final class APIRequestEntityFieldFilterParserFactory
{
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, Integer> integer (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<Integer> selector
  ) {
    return APIRequestEntityFieldFilterParserFactory.integer(parameter, (CriteriaExpressionSelector<Integer>) selector);
  }
  
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, Integer> integer (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<Integer> selector
  ) {
    return new APIRequestEntityFieldFilterParser<Entity, Integer>(
        parameter,
        new IntegerFilterParser(), 
        new CriteriaComparableFilterASTVisitor<Entity, Integer>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, LocalDateTime> datetime (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<LocalDateTime> selector
  ) {
    return APIRequestEntityFieldFilterParserFactory.datetime(parameter, (CriteriaExpressionSelector<LocalDateTime>) selector);
  }
  
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, LocalDateTime> datetime (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> selector
  ) {
    return new APIRequestEntityFieldFilterParser<Entity, LocalDateTime>(
        parameter, 
        new DateTimeFilterParser(), 
        new CriteriaDateTimeFilterASTVisitor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, LocalDateTime> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<LocalDateTime> start,
    @NonNull final CriteriaRootExpressionSelector<LocalDateTime> end
  ) {
    return APIRequestEntityFieldFilterParserFactory.datetimeInRange(
      parameter, 
      (CriteriaExpressionSelector<LocalDateTime>) start, 
      (CriteriaExpressionSelector<LocalDateTime>) end
    );
  }
  
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, LocalDateTime> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> start,
    @NonNull final CriteriaExpressionSelector<LocalDateTime> end
  ) {
    return new APIRequestEntityFieldFilterParser<Entity, LocalDateTime>(
        parameter, 
        new DateTimeFilterParser(), 
        new CriteriaDateTimeInRangeFilterASTVisitor<Entity>(start, end)
    );
  }
  
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, Long> duration (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<Long> selector
  ) {
    return APIRequestEntityFieldFilterParserFactory.duration(parameter, (CriteriaExpressionSelector<Long>) selector);
  }
  
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, Long> duration (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<Long> selector
  ) {
    return new APIRequestEntityFieldFilterParser<Entity, Long>(
        parameter,
        new DurationFilterParser(), 
        new CriteriaComparableFilterASTVisitor<Entity, Long>(selector)
    );
  }
}
