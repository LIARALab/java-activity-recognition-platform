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


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.NodeCollectionRequestConfiguration;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.node.Node_;
import org.liara.api.data.entity.tree.NestedSetCoordinates_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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
  
  public int getRootSetEnd () {
    if (getSize() <= 0) {
      return 1;
    } else {
      final EntityManager manager = getManagerFactory().createEntityManager();
      final int result = manager.createQuery(
        "SELECT MAX(node._coordinates._end) + 1 FROM Node node",
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
      query.andWhere(
        builder.greaterThan(
          queried.get(Node_._coordinates)
                 .get(NestedSetCoordinates_._start),
          node.getCoordinates().getStart()
        )
      );
      query.andWhere(
        builder.lessThan(
          queried.get(Node_._coordinates)
                 .get(NestedSetCoordinates_._end),
          node.getCoordinates().getEnd()
        )
      );
    };
    
    return apply(operator);
  }
  
  public NodeCollection directChildrenOf (@NonNull final Node node) {
    final EntityCollectionOperator<Node> operator = query -> {
      final QueriedEntity<?, Node> queried = query.getEntity();
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.equal(
          queried.get(Node_._coordinates)
                 .get(NestedSetCoordinates_._depth),
          node.getCoordinates().getDepth() + 1
        )
      );
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
        builder.lessThan(
          queried.get(Node_._coordinates)
                 .get(NestedSetCoordinates_._start), 
          node.getCoordinates().getStart()
        ),
        builder.greaterThan(
          queried.get(Node_._coordinates)
                 .get(NestedSetCoordinates_._end),  
          node.getCoordinates().getEnd()
        )
      ));
    };
    
    return apply(operator);
  }
}
