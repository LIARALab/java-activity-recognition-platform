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
package org.liara.api.controller.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.WritableControllerConfiguration;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.schema.NodeSchema;
import org.liara.api.event.NodeEvent;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.filtering.Filter;
import org.liara.rest.error.InvalidModelException;
import org.liara.rest.metamodel.collection.PostableCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;

/**
 * A controller for all API endpoints that read, write or patch information about nodes.
 *
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 */
public final class ChildrenNodeCollectionController
  extends BaseNodeCollectionController
  implements PostableCollection<Node>
{
  @NonNull
  private final WritableControllerConfiguration _configuration;

  @NonNull
  private final Long _parentIdentifier;

  @Autowired
  public ChildrenNodeCollectionController (
    @NonNull final Long parentIdentifier, @NonNull final WritableControllerConfiguration configuration
  )
  {
    super(configuration);
    _parentIdentifier = parentIdentifier;
    _configuration = configuration;
  }

  @Override
  public @NonNull Long post (
    @NonNull @Valid final JsonNode json
  )
  throws JsonProcessingException, InvalidModelException
  {
    @NonNull final NodeSchema schema = _configuration.getObjectMapper().treeToValue(json, NodeSchema.class);

    schema.setParent(_parentIdentifier);

    _configuration.assertIsValid(schema);
    _configuration.getApplicationEventPublisher().publishEvent(new NodeEvent.Create(this, schema));

    return schema.getIdentifier();
  }

  @Override
  public @NonNull JPAEntityCollection<Node> getCollection () {
    @NonNull final Node parent = _configuration.getEntityManager().find(Node.class, _parentIdentifier);

    return (JPAEntityCollection<Node>) Composition.of(
      Filter.expression(":this.coordinates.start > :parentStart")
        .setParameter("parentStart", parent.getCoordinates().getStart()),
      Filter.expression(":this.coordinates.end < :parentEnd")
        .setParameter("parentEnd", parent.getCoordinates().getEnd()),
      Filter.expression(":this.coordinates.depth = :parentDepth - 1")
        .setParameter("parentDepth", parent.getCoordinates().getDepth())
    ).apply(super.getCollection());
  }
}
