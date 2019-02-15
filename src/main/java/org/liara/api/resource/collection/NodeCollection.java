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
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.error.InvalidModelException;
import org.liara.rest.request.RestRequest;
import org.liara.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
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
    super(Node.class, Objects.requireNonNull(builder.getCollectionResourceBuilder()));
    _applicationEventPublisher = Objects.requireNonNull(builder.getApplicationEventPublisher());
    _validator = Objects.requireNonNull(builder.getValidator());
  }

  @Override
  public @NonNull Mono<RestResponse> post (@NonNull final RestRequest request) {
    return request.getBody(NodeSchema.class).flatMap(this::post);
  }

  public @NonNull Mono<RestResponse> post (@NonNull final NodeSchema schema) {
    try {
      assertIsValid(schema);
      _applicationEventPublisher.publishEvent(new NodeEvent.Create(this, schema));

      return Mono.just(
        RestResponse.ofType(Long.class).ofModel(
          Objects.requireNonNull(schema.getIdentifier())
        )
      );
    } catch (@NonNull final InvalidModelException exception) {
      return Mono.error(new IllegalRestRequestException(exception));
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
