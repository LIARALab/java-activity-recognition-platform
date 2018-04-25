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
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;

import org.liara.api.collection.query.EntityCollectionMainQuery;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class      EntityCollectionFieldGroupingTransformation<Entity>
       implements EntityCollectionGroupTransformation<Entity>
{
  @NonNull
  private final EntityFieldSelector<Entity, Expression<?>> _field;
  
  public EntityCollectionFieldGroupingTransformation (
    @NonNull final EntityFieldSelector<Entity, Expression<?>> field
  ) {
    _field = field;
  }
  
  @Override
  public void apply (@NonNull final EntityCollectionMainQuery<Entity, Tuple> query) {
    final Expression<?> group = _field.select(query);
    
    query.andGroupBy(group);
    
    final List<Selection<?>> selections = new ArrayList<>(
      query.getCriteriaQuery().getSelection().getCompoundSelectionItems()
    );
 
    selections.add(group);
    query.getCriteriaQuery().multiselect(selections);
  }

}