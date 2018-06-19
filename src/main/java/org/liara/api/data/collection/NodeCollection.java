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
package org.liara.api.data.collection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.NodeCollectionRequestConfiguration;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.sensor.Sensor_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;

@Component
@DefaultCollectionRequestConfiguration(NodeCollectionRequestConfiguration.class)
public class NodeCollection extends EntityCollection<Node>
{     
  @Autowired
  public NodeCollection (
    @NonNull final EntityManagerFactory entityManagerFactory
  ) { super(entityManagerFactory, Node.class); }

  public NodeCollection (
    @NonNull final NodeCollection toCopy  
  ) { super(toCopy); }
  
  public NodeCollection (
    @NonNull final NodeCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<Node> operator
  ) { super(collection, operator); }
  
  public List<Node> getAllChildren (@NonNull final Node node) {
    final EntityManager manager = getManagerFactory().createEntityManager();
    final TypedQuery<Node> query = manager.createQuery(
      "SELECT child FROM Node child WHERE child._setStart > :start AND child._setEnd < :end", 
      Node.class
    );
    
    query.setParameter("start", node.getCoordinates().getStart());
    query.setParameter("end", node.getCoordinates().getEnd());
    
    return query.getResultList();
  }  

  public List<Node> getAllChildren (@NonNull final Node ...nodes) {
    return this.getAllChildren(Arrays.asList(nodes));
  }
  
  public List<Node> getAllChildren (@NonNull final Iterable<Node> nodes) {
    final EntityManager manager = getManagerFactory().createEntityManager();
    final CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
    final CriteriaQuery<Node> query = criteriaBuilder.createQuery(Node.class);
    
    final Root<Node> child = query.from(Node.class);
    query.select(child);
    query.where(
      criteriaBuilder.or(
        StreamSupport.stream(nodes.spliterator(), false).map(
          node -> {
            return criteriaBuilder.and(
              criteriaBuilder.greaterThan(child.get("_setStart"), node.getCoordinates().getStart()),
              criteriaBuilder.lessThan(child.get("_setEnd"), node.getCoordinates().getEnd())
            );
          }
        ).toArray(size -> new Predicate[size])
      )  
    );
    
    final List<Node> result = manager.createQuery(query).getResultList();
    
    manager.close();
    
    return result;
  }
  
  public List<Node> getAllChildren (@NonNull final long identifier) {
    final EntityManager manager = getManagerFactory().createEntityManager();
    final TypedQuery<Node> query = manager.createQuery(
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
    
    
    final List<Node> result = query.setParameter("identifier", identifier)
                                   .getResultList();
    
    manager.close();
    
    return result;
  }

  /**
   * @todo optimisable
   * @param node
   * @return
   */
  public List<Sensor> getAllSensors (@NonNull final Iterable<Node> parents, @NonNull final String type) {
    final EntityManager manager = getManagerFactory().createEntityManager();
    final CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
    final CriteriaQuery<Sensor> query = criteriaBuilder.createQuery(Sensor.class);
    final List<Node> nodes = this.getAllChildren(parents);
    Iterables.addAll(nodes, parents);
    
    final Root<Sensor> sensor = query.from(Sensor.class);
    query.select(sensor);
    query.where(sensor.join(Sensor_._node).in(nodes));
    query.where(criteriaBuilder.equal(sensor.get(Sensor_._type), type));
    
    final List<Sensor> result = manager.createQuery(query).getResultList();
    
    manager.close();
    
    return result;
  }
  
  public int getRootSetEnd () {
    if (getSize() <= 0) {
      return 1;
    } else {
      final EntityManager manager = getManagerFactory().createEntityManager();
      final int result = manager.createQuery(
        "SELECT MAX(node._setEnd) + 1 FROM Node node",
        Integer.class
      ).getSingleResult().intValue();
      manager.close();
      return result;
    }
  }
  
  public int getRootSetStart () {
    return 0;
  }
  
  public NodeCollection deepChildrenOf (@NonNull final Node node) {
    final EntityCollectionOperator<Node> operator = query -> {
      final QueriedEntity<?, Node> queried = query.getEntity();
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(builder.greaterThan(queried.get("_setStart"), node.getCoordinates().getStart()));
      query.andWhere(builder.lessThan(queried.get("_setEnd"), node.getCoordinates().getEnd()));
    };
    
    return apply(operator);
  }
  
  public NodeCollection directChildrenOf (@NonNull final Node node) {
    final EntityCollectionOperator<Node> operator = query -> {
      final QueriedEntity<?, Node> queried = query.getEntity();
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(builder.equal(queried.get("_depth"), node.getCoordinates().getDepth() + 1));
    };
    
    return deepChildrenOf(node).apply(operator);
  }
  
  @Override
  public NodeCollection apply (@NonNull final EntityCollectionOperator<Node> operator) {
    return new NodeCollection(this, getOperator().conjugate(operator));
  }

  public NodeCollection parentsOf (@NonNull final Node node) {
    final EntityCollectionOperator<Node> operator = query -> {
      final QueriedEntity<?, Node> queried = query.getEntity();
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      
      query.andWhere(builder.and(
        builder.lessThan(queried.get("_setStart"), node.getCoordinates().getStart()),
        builder.greaterThan(queried.get("_setEnd"), node.getCoordinates().getEnd())
      ));
    };
    
    return apply(operator);
  }
}
