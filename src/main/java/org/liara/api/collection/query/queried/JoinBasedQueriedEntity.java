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

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Subquery;

import org.springframework.lang.NonNull;

public class JoinBasedQueriedEntity<Base, Entity> extends FromBasedQueriedEntity<Base, Entity>
{
  @NonNull
  private final Join<Base, Entity> _join;
  
  public JoinBasedQueriedEntity(@NonNull final Join<Base, Entity> join) {
    super(join);
    _join = join;
  }

  @Override
  public QueriedEntity<Base, Entity> correlate (@NonNull final Subquery<?> subquery) {
    return new JoinBasedQueriedEntity<>(subquery.correlate(_join));
  }
}
