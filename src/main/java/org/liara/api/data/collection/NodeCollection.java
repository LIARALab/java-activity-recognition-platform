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
package org.liara.api.data.collection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.liara.api.collection.CompleteEntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.data.collection.configuration.NodeCollectionRequestConfiguration;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;

@Component
@DefaultCollectionRequestConfiguration(NodeCollectionRequestConfiguration.class)
public class NodeCollection extends CompleteEntityCollection<Node, Long>
{     
  @Autowired
  public NodeCollection (
    @NonNull final EntityManager entityManager
  ) {
    super(Node.class, entityManager);
  }
  
  public List<Node> getAllChildren (@NonNull final Node node) {
    final TypedQuery<Node> query = getEntityManager().createQuery(
      "SELECT child FROM Node child WHERE child._setStart > :start AND child._setEnd < :end", 
      Node.class
    );
    
    query.setParameter("start", node.getSetStart());
    query.setParameter("end", node.getSetEnd());
    
    return query.getResultList();
  }  

  public List<Node> getAllChildren (@NonNull final Node ...nodes) {
    return this.getAllChildren(Arrays.asList(nodes));
  }
  
  public List<Node> getAllChildren (@NonNull final Iterable<Node> nodes) {
    final CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
    final CriteriaQuery<Node> query = criteriaBuilder.createQuery(Node.class);
    
    final Root<Node> child = query.from(Node.class);
    query.select(child);
    query.where(
      criteriaBuilder.or(
        StreamSupport.stream(nodes.spliterator(), false).map(
          node -> {
            return criteriaBuilder.and(
              criteriaBuilder.greaterThan(child.get("_setStart"), node.getSetStart()),
              criteriaBuilder.lessThan(child.get("_setEnd"), node.getSetEnd())
            );
          }
        ).toArray(size -> new Predicate[size])
      )  
    );
    
    return getEntityManager().createQuery(query).getResultList();
  }
  
  public List<Node> getAllChildren (@NonNull final long identifier) {
    final TypedQuery<Node> query = getEntityManager().createQuery(
      String.join(
        " ", 
        "SELECT child",
        "FROM Node child, Node parent",
        "WHERE parent._identifier = :identifier",
          "AND child._setStart > parent._setStart",
          "AND child._setEnd < parent._setEnd" 
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
    final CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
    final CriteriaQuery<Sensor> query = criteriaBuilder.createQuery(Sensor.class);
    final List<Node> nodes = this.getAllChildren(parents);
    Iterables.addAll(nodes, parents);
    
    final Root<Sensor> sensor = query.from(Sensor.class);
    query.select(sensor);
    query.where(sensor.join("_nodes").in(nodes));
    query.where(criteriaBuilder.equal(sensor.get("_type"), type));
    
    return getEntityManager().createQuery(query).getResultList();
  }
}
