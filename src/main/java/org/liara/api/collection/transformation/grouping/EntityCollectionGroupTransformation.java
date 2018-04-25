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
package org.liara.api.collection.transformation.grouping;

import java.util.Arrays;

import javax.persistence.Tuple;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.view.EntityCollectionAggregation;
import org.liara.api.collection.view.EntityCollectionGrouping;
import org.springframework.lang.NonNull;

import com.google.common.collect.ImmutableList;

public interface EntityCollectionGroupTransformation<Entity>
{
  public void apply (
    @NonNull final EntityCollectionMainQuery<Entity, Tuple> query
  );
  
  default public EntityCollectionGrouping<Entity> apply (
    @NonNull final EntityCollectionAggregation<Entity, ?> aggregation
  ) {
    return new EntityCollectionGrouping<>(aggregation, this);
  }
  
  default public EntityCollectionGroupTransformation<Entity> apply (
    @NonNull final EntityCollectionGroupTransformation<Entity> group
  ) {
    if (group instanceof EntityCollectionMultipleGroupingTransformation) {
      final ImmutableList.Builder<EntityCollectionGroupTransformation<Entity>> builder = ImmutableList.builder();
      builder.addAll((EntityCollectionMultipleGroupingTransformation<Entity>) group);
      builder.add(this);
      return new EntityCollectionMultipleGroupingTransformation<>(builder.build());
    } else {
      return new EntityCollectionMultipleGroupingTransformation<>(
        Arrays.asList(group, this)
      );
    }
  }
  
  default public EntityCollectionGrouping<Entity> apply (
    @NonNull final EntityCollectionGrouping<Entity> group
  ) {
    return new EntityCollectionGrouping<>(
      group.getAggregation(), 
      apply(group.getGroupTransformation())
    );
  }
}
