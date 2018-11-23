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

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.NodeCollectionRequestConfiguration;
import org.liara.api.data.entity.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

@Component
@DefaultCollectionRequestConfiguration(NodeCollectionRequestConfiguration.class)
public class NodeCollection extends EntityCollection<Node>
{     
  @Autowired
  public NodeCollection (
    @NonNull final EntityManager entityManager
  ) { super(entityManager, Node.class); }

  public NodeCollection (
    @NonNull final NodeCollection toCopy  
  ) { super(toCopy); }
  
  public NodeCollection (
    @NonNull final NodeCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<Node> operator
  ) { super(collection, operator); }
  
  public NodeCollection deepChildrenOf (@NonNull final Node node) {
    final EntityCollectionOperator<Node> operator = query -> {
      final QueriedEntity<?, Node> queried = query.getEntity();
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.greaterThan(
          queried.get("_coordinates").get("_start"),
          node.getCoordinates().getStart()
        )
      );
      query.andWhere(
        builder.lessThan(
          queried.get("_coordinates").get("_end"),
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
          queried.get("_coordinates").get("_depth"),
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
          queried.get("_coordinates").get("_start"),
          node.getCoordinates().getStart()
        ),
        builder.greaterThan(
          queried.get("_coordinates").get("_end"),
          node.getCoordinates().getEnd()
        )
      ));
    };
    
    return apply(operator);
  }
}
