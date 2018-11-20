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
package org.liara.api.controller.rest;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.CollectionFactory;
import org.liara.api.collection.CollectionRequestConfiguration;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class BaseRestController
{
  @NonNull
  private final CollectionFactory _collections;

  public BaseRestController (@NonNull final CollectionFactory collections) {
    _collections = collections;
  }

  protected <Entity> @NonNull Long count (
    @NonNull final Class<Entity> entity, @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return apply(_collections.getCollection(entity), _collections.getConfiguration(entity), request).select(
      "COUNT(:this)",
      Long.class
    ).getSingleResult();
  }

  protected <Entity> @NonNull ResponseEntity<@NonNull List<@NonNull Entity>> index (
    @NonNull final Class<Entity> entity,
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return toResponse(apply(_collections.getCollection(entity), _collections.getConfiguration(entity), request));
  }

  protected <Entity> @NonNull Entity get (
    @NonNull final Class<Entity> entity, @PathVariable final Long identifier
  )
  {
    return _collections.getEntity(entity, identifier);
  }

  protected <Entity> @NonNull ResponseEntity<@NonNull List<@NonNull Entity>> toResponse (
    @NonNull final JPAEntityCollection<Entity> collection
  )
  {
    @NonNull final List<@NonNull Entity> content = collection.find();

    if (content.size() >= collection.select("COUNT(*)", Long.class).getSingleResult()) {
      return new ResponseEntity<>(content, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(content, HttpStatus.PARTIAL_CONTENT);
    }
  }

  protected <Entity> @NonNull JPAEntityCollection<Entity> apply (
    @NonNull final JPAEntityCollection<Entity> collection,
    @NonNull final CollectionRequestConfiguration<Entity> configuration,
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    final APIRequest apiRequest           = new APIRequest(request.getParameterMap());
    final APIRequestValidation validation = configuration.validate(apiRequest);

    validation.throwIfInvalid();

    return (JPAEntityCollection<Entity>) configuration.parse(apiRequest).apply(collection);
  }

  protected @NonNull CollectionFactory getCollections () {
    return _collections;
  }
}
