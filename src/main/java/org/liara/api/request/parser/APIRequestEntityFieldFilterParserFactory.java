package org.liara.api.request.parser;

import java.time.LocalDateTime;

import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.filter.parser.DateTimeFilterParser;
import org.liara.api.filter.parser.IntegerFilterParser;
import org.liara.api.filter.visitor.criteria.CriteriaDateTimeFilterASTVisitor;
import org.liara.api.filter.visitor.criteria.CriteriaIntegerFilterASTVisitor;
import org.springframework.lang.NonNull;

public final class APIRequestEntityFieldFilterParserFactory
{
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, Integer> integer (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<Integer> selector
  ) {
    return new APIRequestEntityFieldFilterParser<Entity, Integer>(
        parameter, 
        selector, 
        new IntegerFilterParser(), 
        new CriteriaIntegerFilterASTVisitor<>()
    );
  }
  
  public static <Entity> APIRequestEntityFieldFilterParser<Entity, LocalDateTime> datetime (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> selector
  ) {
    return new APIRequestEntityFieldFilterParser<Entity, LocalDateTime>(
        parameter, 
        selector, 
        new DateTimeFilterParser(), 
        new CriteriaDateTimeFilterASTVisitor<>()
    );
  }
}
