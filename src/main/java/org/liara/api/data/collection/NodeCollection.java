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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.data.collection.configuration.NodeCollectionRequestConfiguration;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.node.NodeModifier;
import org.liara.api.data.entity.sensor.Sensor;
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
    @NonNull final EntityManager entityManager
  ) {
    super(entityManager, Node.class);
  }
  
  public List<Node> getAllChildren (@NonNull final Node node) {
    final TypedQuery<Node> query = getManager().createQuery(
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
    final CriteriaBuilder criteriaBuilder = getManager().getCriteriaBuilder();
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
    
    return getManager().createQuery(query).getResultList();
  }
  
  public List<Node> getAllChildren (@NonNull final long identifier) {
    final TypedQuery<Node> query = getManager().createQuery(
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
    final CriteriaBuilder criteriaBuilder = getManager().getCriteriaBuilder();
    final CriteriaQuery<Sensor> query = criteriaBuilder.createQuery(Sensor.class);
    final List<Node> nodes = this.getAllChildren(parents);
    Iterables.addAll(nodes, parents);
    
    final Root<Sensor> sensor = query.from(Sensor.class);
    query.select(sensor);
    query.where(sensor.join("_nodes").in(nodes));
    query.where(criteriaBuilder.equal(sensor.get("_type"), type));
    
    return getManager().createQuery(query).getResultList();
  }
  
  @Transactional
  public Node add (@NonNull final NodeModifier modifier) throws EntityNotFoundException {
    int setStart = 0;
    int setEnd = 0;
    
    if (modifier.getParent().isPresent()) {
      final Node parent = findByIdentifierOrFail(modifier.getParent().get());

      setStart = parent.getSetEnd();
      setEnd = parent.getSetEnd() + 1;

      getManager().createQuery(
        "UPDATE Node SET _setStart = _setStart + 2 WHERE _setStart > :parentSetEnd"
      ).setParameter("parentSetEnd", parent.getSetEnd()).executeUpdate();
      
      getManager().createQuery(
        "UPDATE Node SET _setEnd = _setEnd + 2 WHERE _setEnd >= :parentSetEnd"
      ).setParameter("parentSetEnd", parent.getSetEnd()).executeUpdate();
    } else {
      final int rootEnd = getRootSetEnd();
      setStart = rootEnd;
      setEnd = rootEnd + 1;
    }
    
    final Node node = new Node(setStart, setEnd);
    node.setName(modifier.getName().get());
    
    //this.add(node);
    
    return node;
  }
  
  /*
  @Transactional
  public void remove (@NonNull final Node node) {
    node.delete();
    
    getEntityManager().createQuery(
      String.join(
        "", 
        "UPDATE Node",
        "SET _deletionDate = :deletionDate",
        "WHERE _setEnd >= :parentSetEnd",
        "  AND _setStart <= :parentSetStart",
        "  AND _deletionDate IS NULL"
      )
    ).setParameter("deletionDate", node.getDeletionDate())
     .setParameter("parentSetEnd", node.getSetEnd())
     .setParameter("parentSetStart", node.getSetStart())
     .executeUpdate();
    
    getEntityManager().createQuery(String.join(
      "", 
      "UPDATE Sensor",
      "JOIN _nodes",
      "SET _deletionDate = :deletionDate",
      "WHERE _nodes._setEnd >= :parentSetEnd",
      "  AND _nodes._setStart <= :parentSetStart",
      "  AND _deletionDate IS NULL"
    )).setParameter("deletionDate", node.getDeletionDate())
      .setParameter("parentSetEnd", node.getSetEnd())
      .setParameter("parentSetStart", node.getSetStart())
      .executeUpdate();
    
    getEntityManager().createQuery(String.join(
      "", 
      "UPDATE State",
      "JOIN _sensor._nodes AS _nodes",
      "SET _deletionDate = :deletionDate",
      "WHERE _nodes._setEnd >= :parentSetEnd",
      "  AND _nodes._setStart <= :parentSetStart",
      "  AND _deletionDate IS NULL"
    )).setParameter("deletionDate", node.getDeletionDate())
      .setParameter("parentSetEnd", node.getSetEnd())
      .setParameter("parentSetStart", node.getSetStart())
      .executeUpdate();
  }*/
  
  public int getRootSetEnd () {
    if (this.getSize() <= 0) {
      return 1;
    } else {
      return getManager().createQuery(
        "SELECT MAX(node._setEnd) + 1 FROM Node node",
        Integer.class
      ).getSingleResult().intValue();
    }
  }
  
  public int getRootSetStart () {
    return 0;
  }
}
