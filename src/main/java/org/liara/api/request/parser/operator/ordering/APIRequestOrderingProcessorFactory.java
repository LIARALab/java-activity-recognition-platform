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
package org.liara.api.request.parser.operator.ordering;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.CollectionRequestConfiguration;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.springframework.lang.NonNull;

public final class APIRequestOrderingProcessorFactory
{
  public static <Entity> APIRequestOrderingProcessor<Entity> field (
    @NonNull final String key, 
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<?>> selector
  ) {
    return field(key, (EntityFieldSelector<Entity, Expression<?>>) selector);
  }
  
  public static <Entity> APIRequestOrderingProcessor<Entity> field (
    @NonNull final String key, 
    @NonNull final EntityFieldSelector<Entity, Expression<?>> selector
  ) {
    return new APIRequestFieldOrderingProcessor<>(key, selector);
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> joinConfiguration (
    @NonNull final String alias, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return joinConfiguration(
      alias, 
      (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, 
      configuration
    );
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> joinConfiguration (
    @NonNull final String alias, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends CollectionRequestConfiguration<Joined>> configuration
  ) {
    return new APIRequestJoinOrderingProcessor<>(
      alias, join, 
      new APIRequestConfigurationBasedOrderingProcessor<>(configuration)
    );
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> joinCollection (
    @NonNull final String alias, 
    @NonNull final SimpleEntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> configuration
  ) {
    return joinCollection(
      alias, 
      (EntityFieldSelector<Entity, Join<Entity, Joined>>) join, 
      configuration
    );
  }
  
  public static <Entity, Joined> APIRequestOrderingProcessor<Entity> joinCollection (
    @NonNull final String alias, 
    @NonNull final EntityFieldSelector<Entity, Join<Entity, Joined>> join, 
    @NonNull final Class<? extends EntityCollection<Joined>> configuration
  ) {
    return new APIRequestJoinOrderingProcessor<Entity, Joined>(
      alias, join, 
      new APIRequestConfigurationBasedOrderingProcessor<Joined>(
          CollectionRequestConfiguration.getDefaultClass(configuration)
      )
    );
  }
}
