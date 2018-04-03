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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;

import org.domus.api.collection.EntityCollection;
import org.domus.api.collection.EntityCollections;
import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.data.entity.Node;
import org.domus.api.data.entity.Sensor;
import org.domus.api.filter.Filter;

@Component
public class NodeRepository implements Repository<Node, Long>
{
  @NonNull final private EntityManager _entityManager;
  
  @NonNull final private EntityCollections _collections;
  
  @NonNull final private EntityCollection<Node, Long> _fullCollection;
 
  @Autowired
  public NodeRepository (
    @NonNull final ApplicationContext context
  ) {
    _entityManager = context.getBean(EntityManager.class);
    _collections = context.getBean(EntityCollections.class);
    _fullCollection = _collections.createCollection(Node.class);
  }
  
  public EntityCollection<Node, Long> createCollection () {
    return _fullCollection;
  }
  
  public EntityCollection<Node, Long> createCollection (@NonNull final Filter<Node> filter) {
    return _collections.createCollection(Node.class, filter);
  }
  
  public Node findById (final long identifier) {
    return _fullCollection.findById(identifier);
  }
  
  public Node findByIdOrFail (final long identifier) throws EntityNotFoundException {
    return _fullCollection.findByIdOrFail(identifier);
  }
  
  public List<Node> getChildren (@NonNull final Node node) {
    final TypedQuery<Node> query = _entityManager.createQuery(
      "SELECT child FROM Node child WHERE child._start > :start AND child._end < :end", 
      Node.class
    );
    
    query.setParameter("start", node.getStart());
    query.setParameter("end", node.getEnd());
    
    return query.getResultList();
  }  

  public List<Node> getChildren (@NonNull final Node ...nodes) {
    return this.getChildren(Arrays.asList(nodes));
  }
  
  public List<Node> getChildren (@NonNull final Iterable<Node> nodes) {
    final CriteriaBuilder criteriaBuilder = _entityManager.getCriteriaBuilder();
    final CriteriaQuery<Node> query = criteriaBuilder.createQuery(Node.class);
    
    final Root<Node> child = query.from(Node.class);
    query.select(child);
    query.where(
      criteriaBuilder.or(
        StreamSupport.stream(nodes.spliterator(), false).map(
          node -> {
            return criteriaBuilder.and(
              criteriaBuilder.greaterThan(child.get("_start"), node.getStart()),
              criteriaBuilder.lessThan(child.get("_end"), node.getEnd())
            );
          }
        ).toArray(size -> new Predicate[size])
      )  
    );
    
    return _entityManager.createQuery(query).getResultList();
  }
  
  public List<Node> getChildren (@NonNull final long identifier) {
    final TypedQuery<Node> query = _entityManager.createQuery(
      String.join(
        " ", 
        "SELECT child",
        "FROM Node child, Node parent",
        "WHERE parent._identifier = :identifier",
          "AND child._start > parent._start",
          "AND child._end < parent._end" 
      ),
      Node.class
    );
    
    query.setParameter("identifier", identifier);
    
    return query.getResultList();
  }

  /**
   * @todo optimisable
   * @param node
   * @return
   */
  public List<Sensor> getAllSensors (@NonNull final Iterable<Node> parents, @NonNull final String type) {
    final CriteriaBuilder criteriaBuilder = _entityManager.getCriteriaBuilder();
    final CriteriaQuery<Sensor> query = criteriaBuilder.createQuery(Sensor.class);
    final List<Node> nodes = this.getChildren(parents);
    Iterables.addAll(nodes, parents);
    
    final Root<Sensor> sensor = query.from(Sensor.class);
    query.select(sensor);
    query.where(sensor.join("_nodes").in(nodes));
    query.where(criteriaBuilder.equal(sensor.get("_type"), type));
    
    return _entityManager.createQuery(query).getResultList();
  }
}
