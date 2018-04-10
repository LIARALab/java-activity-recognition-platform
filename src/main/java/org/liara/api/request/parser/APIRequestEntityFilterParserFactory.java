package org.liara.api.request.parser;

import java.time.LocalDateTime;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.criteria.CriteriaRootExpressionSelector;
import org.liara.api.filter.parser.DateTimeFilterParser;
import org.liara.api.filter.parser.DurationFilterParser;
import org.liara.api.filter.parser.IntegerFilterParser;
import org.liara.api.filter.parser.TextFilterParser;
import org.liara.api.filter.visitor.criteria.CriteriaDateTimeFilterASTVisitor;
import org.liara.api.filter.visitor.criteria.CriteriaDateTimeInRangeFilterASTVisitor;
import org.liara.api.filter.visitor.criteria.CriteriaTextFilterASTVisitor;
import org.liara.api.filter.visitor.criteria.CriteriaComparableFilterASTVisitor;
import org.springframework.lang.NonNull;

public final class APIRequestEntityFilterParserFactory
{
  public static <Entity> APIRequestEntityFilterParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<Integer> selector
  ) {
    return APIRequestEntityFilterParserFactory.integer(parameter, (CriteriaExpressionSelector<Integer>) selector);
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<Integer> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, Integer>(
        parameter,
        new IntegerFilterParser(), 
        new CriteriaComparableFilterASTVisitor<Entity, Integer>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<LocalDateTime> selector
  ) {
    return APIRequestEntityFilterParserFactory.datetime(parameter, (CriteriaExpressionSelector<LocalDateTime>) selector);
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, LocalDateTime>(
        parameter, 
        new DateTimeFilterParser(), 
        new CriteriaDateTimeFilterASTVisitor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<LocalDateTime> start,
    @NonNull final CriteriaRootExpressionSelector<LocalDateTime> end
  ) {
    return APIRequestEntityFilterParserFactory.datetimeInRange(
      parameter, 
      (CriteriaExpressionSelector<LocalDateTime>) start, 
      (CriteriaExpressionSelector<LocalDateTime>) end
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> start,
    @NonNull final CriteriaExpressionSelector<LocalDateTime> end
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, LocalDateTime>(
        parameter, 
        new DateTimeFilterParser(), 
        new CriteriaDateTimeInRangeFilterASTVisitor<Entity>(start, end)
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<Long> selector
  ) {
    return APIRequestEntityFilterParserFactory.duration(parameter, (CriteriaExpressionSelector<Long>) selector);
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<Long> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, Long>(
        parameter,
        new DurationFilterParser(), 
        new CriteriaComparableFilterASTVisitor<Entity, Long>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final CriteriaRootExpressionSelector<String> selector
  ) {
    return APIRequestEntityFilterParserFactory.text(parameter, (CriteriaExpressionSelector<String>) selector);
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<String> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, String>(
        parameter,
        new TextFilterParser(), 
        new CriteriaTextFilterASTVisitor<Entity>(selector)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityFilterParser<Entity> join (
    @NonNull final String parameter, 
    @NonNull final String join, 
    @NonNull final CollectionRequestConfiguration<Joined> configuration
  ) {
    return new APIRequestJoinBasedEntityFilterParser<>(parameter, join, configuration);
  }
}
