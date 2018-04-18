package org.liara.api.request.parser.filtering;

import java.time.LocalDateTime;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.criteria.CriteriaExpressionSelector;
import org.liara.api.criteria.SimplifiedCriteriaExpressionSelector;
import org.liara.api.criteria.CustomCollectionRelation;
import org.liara.api.filter.parser.DateTimeFilterParser;
import org.liara.api.filter.parser.DurationFilterParser;
import org.liara.api.filter.parser.IntegerFilterParser;
import org.liara.api.filter.parser.TextFilterParser;
import org.liara.api.filter.visitor.collection.EntityCollectionComparableFilterVisitor;
import org.liara.api.filter.visitor.collection.EntityCollectionDateTimeFilterVisitor;
import org.liara.api.filter.visitor.collection.EntityCollectionDateTimeInRangeFilterVisitor;
import org.liara.api.filter.visitor.collection.EntityCollectionTextFilterVisitor;
import org.springframework.lang.NonNull;

public final class APIRequestEntityFilterParserFactory
{
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<Integer> selector
  ) {
    return APIRequestEntityFilterParserFactory.integer(parameter, (CriteriaExpressionSelector<Integer>) selector);
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<Integer> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, Integer>(
        parameter,
        new IntegerFilterParser(), 
        new EntityCollectionComparableFilterVisitor<Entity, Integer>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<LocalDateTime> selector
  ) {
    return APIRequestEntityFilterParserFactory.datetime(parameter, (CriteriaExpressionSelector<LocalDateTime>) selector);
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, LocalDateTime>(
        parameter, 
        new DateTimeFilterParser(), 
        new EntityCollectionDateTimeFilterVisitor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<LocalDateTime> start,
    @NonNull final SimplifiedCriteriaExpressionSelector<LocalDateTime> end
  ) {
    return APIRequestEntityFilterParserFactory.datetimeInRange(
      parameter, 
      (CriteriaExpressionSelector<LocalDateTime>) start, 
      (CriteriaExpressionSelector<LocalDateTime>) end
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<LocalDateTime> start,
    @NonNull final CriteriaExpressionSelector<LocalDateTime> end
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, LocalDateTime>(
        parameter, 
        new DateTimeFilterParser(), 
        new EntityCollectionDateTimeInRangeFilterVisitor<Entity>(start, end)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<Long> selector
  ) {
    return APIRequestEntityFilterParserFactory.duration(parameter, (CriteriaExpressionSelector<Long>) selector);
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<Long> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, Long>(
        parameter,
        new DurationFilterParser(), 
        new EntityCollectionComparableFilterVisitor<Entity, Long>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final SimplifiedCriteriaExpressionSelector<String> selector
  ) {
    return APIRequestEntityFilterParserFactory.text(parameter, (CriteriaExpressionSelector<String>) selector);
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final CriteriaExpressionSelector<String> selector
  ) {
    return new APIRequestASTBasedEntityFilterParser<Entity, String>(
        parameter,
        new TextFilterParser(), 
        new EntityCollectionTextFilterVisitor<Entity>(selector)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionFilteringOperatorParser<Entity> joinConfiguration (
    @NonNull final String parameter, 
    @NonNull final String join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestJoinBasedEntityFilterParser<>(parameter, join, configuration);
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionFilteringOperatorParser<Entity> joinCollection (
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
  
  public static <Entity, Joined> APIRequestEntityCollectionFilteringOperatorParser<Entity> havingConfiguration (
    @NonNull final String parameter, 
    @NonNull final String join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestHavingBasedEntityFilterParser<>(parameter, join, configuration);
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionFilteringOperatorParser<Entity> havingCollection (
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
  
  public static <Entity, Joined> APIRequestEntityCollectionFilteringOperatorParser<Entity> customHavingConfiguration (
    @NonNull final String parameter, 
    @NonNull final Class<Joined> joined,
    @NonNull final CustomCollectionRelation<Entity, Joined> relation,
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestCustomHavingBasedEntityFilterParser<>(parameter, joined, relation, configuration);
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionFilteringOperatorParser<Entity> customHavingCollection (
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
