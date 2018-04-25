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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.springframework.lang.NonNull;

import com.google.common.collect.ImmutableList;

public class      EntityCollectionMultipleGroupingTransformation<Entity> 
       implements EntityCollectionGroupTransformation<Entity>, Iterable<EntityCollectionGroupTransformation<Entity>>
{
  @NonNull
  private final List<EntityCollectionGroupTransformation<Entity>> _groups;

  public EntityCollectionMultipleGroupingTransformation () {
    _groups = ImmutableList.of();
  }

  public EntityCollectionMultipleGroupingTransformation (
    @NonNull final Iterator<EntityCollectionGroupTransformation<Entity>> groups
  ) {
    _groups = ImmutableList.copyOf(groups);
  }

  public EntityCollectionMultipleGroupingTransformation (
    @NonNull final Iterable<EntityCollectionGroupTransformation<Entity>> groups
  ) {
    _groups = ImmutableList.copyOf(groups);
  }
  
  public EntityCollectionMultipleGroupingTransformation (
    @NonNull final EntityCollectionGroupTransformation<Entity>[] groups
  ) {
    _groups = ImmutableList.copyOf(groups);
  }

  public EntityCollectionMultipleGroupingTransformation (
    @NonNull final ImmutableList<EntityCollectionGroupTransformation<Entity>> groups
  ) {
    _groups = groups;
  }
  
  public EntityCollectionMultipleGroupingTransformation (
    @NonNull final EntityCollectionGroupTransformation<Entity> group
  ) {
    if (group instanceof EntityCollectionMultipleGroupingTransformation) {
      final EntityCollectionMultipleGroupingTransformation<Entity> groups = (EntityCollectionMultipleGroupingTransformation<Entity>) group;
      _groups = ImmutableList.copyOf(groups);
    } else {
      _groups = ImmutableList.of(group);
    }
  }
      
  @Override
  public void apply (
    @NonNull final EntityCollectionMainQuery<Entity, Tuple> query
  ) {
    for (EntityCollectionGroupTransformation<Entity> group : _groups) {
      group.apply(query);
    }
    
    final List<Selection<?>> selection = new ArrayList<>(
        query.getCriteriaQuery().getSelection().getCompoundSelectionItems()
    );
    
    final int groups = query.getGroupList().size();
    
    for (int index = 1; index <= groups; ++index) {
      selection.set(
        selection.size() - index, 
        selection.get(selection.size() - index).alias("key_" + index)
      );
    }
    
    query.getCriteriaQuery().multiselect(selection);
  }

  @Override
  public Iterator<EntityCollectionGroupTransformation<Entity>> iterator () {
    return _groups.iterator();
  }
  
  public List<EntityCollectionGroupTransformation<Entity>> getGroups () {
    return _groups;
  }
}
