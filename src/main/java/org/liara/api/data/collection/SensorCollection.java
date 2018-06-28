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

import java.util.Collection;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.configuration.DefaultCollectionRequestConfiguration;
import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.data.collection.configuration.SensorCollectionRequestConfiguration;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.node.Node_;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.sensor.Sensor_;
import org.liara.api.data.entity.tree.NestedSetCoordinates_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@DefaultCollectionRequestConfiguration(SensorCollectionRequestConfiguration.class)
public class SensorCollection extends EntityCollection<Sensor>
{ 
  @Autowired
  public SensorCollection (
    @NonNull final EntityManager entityManager
  ) { super(entityManager, Sensor.class); }
  
  public SensorCollection (
    @NonNull final SensorCollection toCopy  
  ) { super(toCopy); }
  
  public SensorCollection (
    @NonNull final SensorCollection collection,
    @NonNull final EntityCollectionConjunctionOperator<Sensor> operator
  ) { super(collection, operator); }
  
  @Override
  public SensorCollection apply (@NonNull final EntityCollectionOperator<Sensor> operator) {
    return new SensorCollection(this, getOperator().conjugate(operator));
  }
  
  public SensorCollection in (@NonNull final Node node) {
    final EntityCollectionOperator<Sensor> operator = query -> query.andWhere(
      query.getEntity().get(Sensor_._node).in(node)
    );
    return apply(operator);
  }

  public SensorCollection deepIn (@NonNull final Node node) {
    final EntityCollectionOperator<Sensor> operator = query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      final Join<Sensor, Node> join = query.getEntity().join(Sensor_._node);
      query.andWhere(
        builder.greaterThanOrEqualTo(
          join.get(Node_._coordinates).get(NestedSetCoordinates_._start), 
          node.getCoordinates().getStart()
        )
      );
      query.andWhere(
        builder.lessThanOrEqualTo(
          join.get(Node_._coordinates).get(NestedSetCoordinates_._end), 
          node.getCoordinates().getEnd()
        )
      );
    };
    
    return apply(operator);
  }
  
  public SensorCollection ofType (@NonNull final Class<?> type) {
    final EntityCollectionOperator<Sensor> operator = query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(builder.equal(query.getEntity().get(Sensor_._type), type.getName()));
    };
    
    return apply(operator);
  }
  
  public SensorCollection ofType (@NonNull final Collection<Class<?>> types) {
    final EntityCollectionOperator<Sensor> operator = query -> {
      query.andWhere(
        query.getEntity().get(Sensor_._type).in(
          types.stream().map(Class::getName)
                        .collect(Collectors.toList())
        )
      );
    };
    
    return apply(operator);
  }
}
