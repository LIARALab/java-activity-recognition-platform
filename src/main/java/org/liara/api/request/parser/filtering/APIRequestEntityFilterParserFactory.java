package org.liara.api.request.parser.filtering;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.filter.interpretor.DatetimeFilterInterpretor;
import org.liara.api.filter.interpretor.DatetimeInRangeFilterInterpretor;
import org.liara.api.filter.interpretor.IntegerFilterInterpretor;
import org.liara.api.filter.interpretor.LongFilterInterpretor;
import org.liara.api.filter.interpretor.TextFilterInterpretor;
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
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Integer>> selector
  ) {
    return APIRequestEntityFilterParserFactory.integer(
      parameter, (EntityFieldSelector<Entity, Expression<Integer>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<Integer>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Integer>(
        parameter, new IntegerFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<ZonedDateTime>> selector
  ) {
    return APIRequestEntityFilterParserFactory.datetime(
      parameter, (EntityFieldSelector<Entity, Expression<ZonedDateTime>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, ZonedDateTime>(
        parameter,
        new DatetimeFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<ZonedDateTime>> start,
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<ZonedDateTime>> end
  ) {
    return APIRequestEntityFilterParserFactory.datetimeInRange(
      parameter, 
      (EntityFieldSelector<Entity, Expression<ZonedDateTime>>) start, 
      (EntityFieldSelector<Entity, Expression<ZonedDateTime>>) end
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> start,
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> end
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, ZonedDateTime>(
        parameter, 
        new DatetimeInRangeFilterInterpretor<>(start, end)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Long>> selector
  ) {
    return APIRequestEntityFilterParserFactory.duration(
      parameter, (EntityFieldSelector<Entity, Expression<Long>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<Long>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Long>(
        parameter,
        new LongFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<String>> selector
  ) {
    return APIRequestEntityFilterParserFactory.text(
      parameter, (EntityFieldSelector<Entity, Expression<String>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionFilteringOperatorParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<String>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, String>(
        parameter,
        new TextFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionFilteringOperatorParser<Entity> joinConfiguration (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return APIRequestEntityFilterParserFactory.joinConfiguration(
      parameter, (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, configuration
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionFilteringOperatorParser<Entity> joinConfiguration (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestEntityCollectionJoinOperatorParser<Entity, Joined>(parameter, join, configuration);
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
