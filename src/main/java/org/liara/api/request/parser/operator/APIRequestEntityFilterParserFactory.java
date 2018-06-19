/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.request.parser.operator;

import java.time.ZonedDateTime;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.relation.EntityRelation;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.filter.interpretor.BooleanFilterInterpretor;
import org.liara.api.filter.interpretor.DatetimeFilterInterpretor;
import org.liara.api.filter.interpretor.DatetimeInRangeFilterInterpretor;
import org.liara.api.filter.interpretor.DoubleFilterInterpretor;
import org.liara.api.filter.interpretor.IntegerFilterInterpretor;
import org.liara.api.filter.interpretor.DurationFilterInterpretor;
import org.liara.api.filter.interpretor.TextFilterInterpretor;
import org.springframework.lang.NonNull;

public final class APIRequestEntityFilterParserFactory
{  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> callback (
    @NonNull final String parameter, 
    @NonNull final APIRequestEntityCollectionCallbackOperatorParser<Entity> callback
  ) {
    return new APIRequestEntityCollectionIfPresentOperatorParser<>(parameter, callback);
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> defaultValue (
    @NonNull final String parameter,
    @NonNull final APIRequestEntityCollectionOperatorParser<Entity> parser,
    @NonNull final EntityCollectionOperator<Entity> operator
  ) {
    return new APIRequestDefaultOperatorParser<>(parameter, operator, parser);
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> booleanValue (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Boolean>> selector
  ) {
    return APIRequestEntityFilterParserFactory.booleanValue(
      parameter, (EntityFieldSelector<Entity, Expression<Boolean>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> booleanValue (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<Boolean>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Boolean>(
        parameter, new BooleanFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> integer (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Integer>> selector
  ) {
    return APIRequestEntityFilterParserFactory.integerValue(
      parameter, (EntityFieldSelector<Entity, Expression<Integer>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> integerValue (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<Integer>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Integer>(
        parameter, new IntegerFilterInterpretor<>(selector)
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> doubleValue (
    @NonNull final String parameter, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Double>> selector
  ) {
    return APIRequestEntityFilterParserFactory.doubleValue(
      parameter, (EntityFieldSelector<Entity, Expression<Double>>) selector
    );
  }
  
  public static <Entity> APIRequestEntityCollectionOperatorParser<Entity> doubleValue (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Expression<Double>> selector
  ) {
    return new APIRequestEntityCollectionCommandBasedFilteringOperatorParser<Entity, Double>(
        parameter, new DoubleFilterInterpretor<>(selector)
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
        new DurationFilterInterpretor<>(selector)
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

    return joinCollection(
      parameter, 
      (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, 
      collection
    );
  }
  
  public static <Entity, Joined> APIRequestEntityCollectionOperatorParser<Entity> joinCollection (
    @NonNull final String parameter, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> collection
  ) {
    return new APIRequestEntityCollectionJoinOperatorParser<Entity, Joined>(
        parameter, join, 
        new APIRequestConfigurationBasedFilteringOperatorParser<Joined>(
          CollectionRequestConfiguration.getDefaultClass(collection)
        )
    );
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
    return new APIRequestEntityCollectionExistsOperatorParser<Entity, Joined>(
        parameter, joined, relation,
        new APIRequestConfigurationBasedFilteringOperatorParser<Joined>(
          CollectionRequestConfiguration.getDefaultClass(collection)
        )
    );
  }
}
