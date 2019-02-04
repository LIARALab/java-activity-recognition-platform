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
package org.liara.api.resource.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.schema.NodeSchema;
import org.liara.api.event.NodeEvent;
import org.liara.api.resource.CollectionResource;
import org.liara.api.utils.Builder;
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.error.InvalidModelException;
import org.liara.rest.request.RestRequest;
import org.liara.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * A controller for all API endpoints that read, write or patch information about nodes.
 *
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric
 * .demongivert@gmail.com)
 */
@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public final class NodeCollection
  extends CollectionResource<Node>
{
  @NonNull
  private final ApplicationEventPublisher _applicationEventPublisher;

  @NonNull
  private final Validator _validator;

  @Autowired
  public NodeCollection (
    @NonNull final NodeCollectionBuilder builder
  ) {
    super(Node.class, Builder.require(builder.getCollectionResourceBuilder()));
    _applicationEventPublisher = Builder.require(builder.getApplicationEventPublisher());
    _validator = Builder.require(builder.getValidator());
  }

  @Override
  public @NonNull RestResponse post (@NonNull final RestRequest request)
  throws IllegalRestRequestException {
    try {
      @NonNull final NodeSchema schema = request.getBody(NodeSchema.class);

      assertIsValid(schema);
      _applicationEventPublisher.publishEvent(new NodeEvent.Create(this, schema));

      return RestResponse.ofType(Node.class).ofModel(
        getEntityManager().find(Node.class, schema.getIdentifier())
      );
    } catch (@NonNull final InvalidModelException exception) {
      throw new IllegalRestRequestException(exception);
    }
  }

  public void assertIsValid (@NonNull final Object object)
  throws InvalidModelException {
    @NonNull final Set<@NonNull ConstraintViolation<@NonNull Object>> errors = _validator.validate(
      object);

    if (errors.size() > 0) {
      throw new InvalidModelException(errors);
    }
  }
}
