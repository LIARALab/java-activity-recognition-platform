/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.liara.api.data.repository;

import javax.persistence.EntityManager;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.EntityCollections;
import org.liara.api.collection.exception.EntityNotFoundException;
import org.liara.api.collection.filtering.EntityCollectionFilter;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.PresenceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PresenceStateRepository implements Repository<PresenceState, Long>
{
  @NonNull final private EntityManager _entityManager;
  
  @NonNull final private EntityCollections _collections;
  
  @NonNull final private EntityCollection<PresenceState, Long> _fullCollection;
 
  @Autowired
  public PresenceStateRepository (
    @NonNull final ApplicationContext context
  ) {
    _entityManager = context.getBean(EntityManager.class);
    _collections = context.getBean(EntityCollections.class);
    _fullCollection = _collections.createCollection(PresenceState.class);
  }
  
  public EntityCollection<PresenceState, Long> createCollection () {
    return _fullCollection;
  }
  
  public EntityCollection<Node, Long> createCollection (@NonNull final EntityCollectionFilter<Node> filter) {
    return _collections.createCollection(Node.class, filter);
  }
  
  public PresenceState findById (final long identifier) {
    return _fullCollection.findById(identifier);
  }
  
  public PresenceState findByIdOrFail (final long identifier) throws EntityNotFoundException {
    return _fullCollection.findByIdOrFail(identifier);
  }
}
