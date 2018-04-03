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
package org.domus.api.data.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.domus.api.collection.EntityCollection;
import org.domus.api.collection.EntityCollections;
import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.data.entity.Node;
import org.domus.api.data.entity.Sensor;
import org.domus.api.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;

@Component
public class SensorRepository implements Repository<Sensor, Long>
{
  @NonNull final private EntityManager _entityManager;
  
  @NonNull final private EntityCollections _collections;
  
  @NonNull final private EntityCollection<Sensor, Long> _fullCollection;
 
  @Autowired
  public SensorRepository (
    @NonNull final ApplicationContext context
  ) {
    _entityManager = context.getBean(EntityManager.class);
    _collections = context.getBean(EntityCollections.class);
    _fullCollection = _collections.createCollection(Sensor.class);
  }
  
  public EntityCollection<Sensor, Long> createCollection () {
    return _fullCollection;
  }
  
  public EntityCollection<Sensor, Long> createCollection (@NonNull final Filter<Sensor> filter) {
    return _collections.createCollection(Sensor.class, filter);
  }
  
  public Sensor findById (final long identifier) {
    return _fullCollection.findById(identifier);
  }
  
  public Sensor findByIdOrFail (final long identifier) throws EntityNotFoundException {
    return _fullCollection.findByIdOrFail(identifier);
  }
}
