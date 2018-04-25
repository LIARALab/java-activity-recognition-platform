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
package org.liara.api.collection.query.queried;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.SelectionImplementor;
import org.springframework.lang.NonNull;

public interface QueriedEntity<Base, Entity> 
       extends   From<Base, Entity>, Renderable, SelectionImplementor<Entity>
{
  public static <Entity> QueriedEntity<Entity, Entity> from (@NonNull final Root<Entity> root) {
    return new RootBasedQueriedEntity<>(root);
  }
  
  public static <Base, Entity> QueriedEntity<Base, Entity> from (@NonNull final Join<Base, Entity> join) {
    return new JoinBasedQueriedEntity<>(join);
  }
  
  public QueriedEntity<Base, Entity> correlate (@NonNull final Subquery<?> subquery);
}
