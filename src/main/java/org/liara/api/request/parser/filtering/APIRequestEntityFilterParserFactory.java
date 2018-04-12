package org.liara.api.request.parser.filtering;

import java.time.LocalDateTime;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.criteria.CustomCollectionRelation;
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
    @NonNull final CriteriaExpressionSelector<Integer> selector
  ) {
    return APIRequestEntityFilterParserFactory.integer(parameter, (SimplifiedCriteriaExpressionSelector<Integer>) selector);
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<Integer> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, Integer>(
        parameter,
        new IntegerFilterParser(), 
        new CriteriaComparableFilterASTVisitor<Entity, Integer>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> selector
  ) {
    return APIRequestEntityFilterParserFactory.datetime(parameter, (SimplifiedCriteriaExpressionSelector<LocalDateTime>) selector);
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<LocalDateTime> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, LocalDateTime>(
        parameter, 
        new DateTimeFilterParser(), 
        new CriteriaDateTimeFilterASTVisitor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> start,
    @NonNull final CriteriaExpressionSelector<LocalDateTime> end
  ) {
    return APIRequestEntityFilterParserFactory.datetimeInRange(
      parameter, 
      (SimplifiedCriteriaExpressionSelector<LocalDateTime>) start, 
      (SimplifiedCriteriaExpressionSelector<LocalDateTime>) end
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<LocalDateTime> start,
    @NonNull final SimplifiedCriteriaExpressionSelector<LocalDateTime> end
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, LocalDateTime>(
        parameter, 
        new DateTimeFilterParser(), 
        new CriteriaDateTimeInRangeFilterASTVisitor<Entity>(start, end)
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<Long> selector
  ) {
    return APIRequestEntityFilterParserFactory.duration(parameter, (SimplifiedCriteriaExpressionSelector<Long>) selector);
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<Long> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, Long>(
        parameter,
        new DurationFilterParser(), 
        new CriteriaComparableFilterASTVisitor<Entity, Long>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<String> selector
  ) {
    return APIRequestEntityFilterParserFactory.text(parameter, (SimplifiedCriteriaExpressionSelector<String>) selector);
  }
  
  public static <Entity> APIRequestEntityFilterParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<String> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, String>(
        parameter,
        new TextFilterParser(), 
        new CriteriaTextFilterASTVisitor<Entity>(selector)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityFilterParser<Entity> joinConfiguration (
    @NonNull final String parameter, 
    @NonNull final String join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestJoinBasedEntityFilterParser<>(parameter, join, configuration);
  }
  
  public static <Entity, Joined> APIRequestEntityFilterParser<Entity> joinCollection (
    @NonNull final String parameter, 
    @NonNull final String join, 
    @NonNull final Class<? extends EntityCollection<Joined, ?>> configuration
  ) {
    return new APIRequestJoinBasedEntityFilterParser<>(
        parameter, 
        join, 
        CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityFilterParser<Entity> havingConfiguration (
    @NonNull final String parameter, 
    @NonNull final String join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestHavingBasedEntityFilterParser<>(parameter, join, configuration);
  }
  
  public static <Entity, Joined> APIRequestEntityFilterParser<Entity> havingCollection (
    @NonNull final String parameter, 
    @NonNull final String join, 
    @NonNull final Class<? extends EntityCollection<Joined, ?>> configuration
  ) {
    return new APIRequestHavingBasedEntityFilterParser<Entity, Joined>(
        parameter,
        join, 
        CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityFilterParser<Entity> customHavingConfiguration (
    @NonNull final String parameter, 
    @NonNull final Class<Joined> joined,
    @NonNull final CustomCollectionRelation<Entity, Joined> relation,
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestCustomHavingBasedEntityFilterParser<>(parameter, joined, relation, configuration);
  }
  
  public static <Entity, Joined> APIRequestEntityFilterParser<Entity> customHavingCollection (
    @NonNull final String parameter, 
    @NonNull final Class<Joined> joined,
    @NonNull final CustomCollectionRelation<Entity, Joined> relation,
    @NonNull final Class<? extends EntityCollection<Joined, ?>> configuration
  ) {
    return new APIRequestCustomHavingBasedEntityFilterParser<Entity, Joined>(
        parameter, joined, relation, CollectionRequestConfiguration.getDefaultClass(configuration)
    );
  }
}
