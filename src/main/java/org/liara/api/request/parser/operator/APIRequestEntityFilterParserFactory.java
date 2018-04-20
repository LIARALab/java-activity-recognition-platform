package org.liara.api.request.parser.operator;

import java.time.ZonedDateTime;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.relation.EntityRelation;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.filter.interpretor.DatetimeFilterInterpretor;
import org.liara.api.filter.interpretor.DatetimeInRangeFilterInterpretor;
import org.liara.api.filter.interpretor.IntegerFilterInterpretor;
import org.liara.api.filter.interpretor.LongFilterInterpretor;
import org.liara.api.filter.interpretor.TextFilterInterpretor;
import org.springframework.lang.NonNull;

public final class APIRequestEntityFilterParserFactory
{
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Integer>> selector
  ) {
    return APIRequestEntityFilterParserFactory.integer(
      parameter, (EntityFieldSelector<Entity, Expression<Integer>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<Integer>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Integer>(
        parameter, new IntegerFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<ZonedDateTime>> selector
  ) {
    return APIRequestEntityFilterParserFactory.datetime(
      parameter, (EntityFieldSelector<Entity, Expression<ZonedDateTime>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> datetime (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, ZonedDateTime>(
        parameter,
        new DatetimeFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> datetimeInRange (
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
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> datetimeInRange (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> start,
    @NonNull final EntityFieldSelector<Entity, Expression<ZonedDateTime>> end
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, ZonedDateTime>(
        parameter, 
        new DatetimeInRangeFilterInterpretor<>(start, end)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Long>> selector
  ) {
    return APIRequestEntityFilterParserFactory.duration(
      parameter, (EntityFieldSelector<Entity, Expression<Long>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> duration (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<Long>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Long>(
        parameter,
        new LongFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<String>> selector
  ) {
    return APIRequestEntityFilterParserFactory.text(
      parameter, (EntityFieldSelector<Entity, Expression<String>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> text (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<String>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, String>(
        parameter,
        new TextFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionOperatorParser<Entity> joinCollection (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> collection
  ) {

    return joinConfiguration(
      parameter, 
      (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, 
      CollectionRequestConfiguration.getDefaultClass(collection)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionOperatorParser<Entity> joinCollection (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> collection
  ) {
    return joinConfiguration(parameter, join, CollectionRequestConfiguration.getDefaultClass(collection));
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionOperatorParser<Entity> joinConfiguration (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return APIRequestEntityFilterParserFactory.joinConfiguration(
      parameter, (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, configuration
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionOperatorParser<Entity> joinConfiguration (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestEntityCollectionJoinOperatorParser<Entity, Joined>(
        parameter, join, 
        new APIRequestConfigurationBasedFilteringOperatorParser<>(configuration)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionOperatorParser<Entity> existsConfiguration (
    @NonNull final String parameter, 
    @NonNull final Class<Joined> joined,
    @NonNull final EntityRelation<Entity, Joined> relation, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestEntityCollectionExistsOperatorParser<>(
        parameter, joined, relation,
        new APIRequestConfigurationBasedFilteringOperatorParser<>(configuration)
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionOperatorParser<Entity> existsCollection (
    @NonNull final String parameter, 
    @NonNull final Class<Joined> joined,
    @NonNull final EntityRelation<Entity, Joined> relation, 
    @NonNull final Class<? extends EntityCollection<Joined>> collection
  ) {
    return existsConfiguration(
        parameter, joined, relation,
        CollectionRequestConfiguration.getDefaultClass(collection)
    );
  }
}
