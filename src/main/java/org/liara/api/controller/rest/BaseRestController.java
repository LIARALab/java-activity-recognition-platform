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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.collection.CollectionController;
import org.liara.api.collection.configuration.RequestConfiguration;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

public abstract class BaseRestController<Entity>
  implements CollectionController<Entity>
{
  @NonNull
  private final RestCollectionControllerConfiguration _configuration;

  @Autowired
  public BaseRestController (@NonNull final RestCollectionControllerConfiguration configuration) {
    _configuration = configuration;
  }

  protected @NonNull Long count (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return select(request).select("COUNT(:this)", Long.class).getSingleResult();
  }

  protected @NonNull ResponseEntity<@NonNull List<@NonNull Entity>> index (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return toResponse(select(request));
  }

  protected @NonNull Entity get (
    @NonNull final Long identifier
  )
  {
    @Nullable final Entity result = Objects.requireNonNull(_configuration.getEntityManager())
                                      .find(getCollection().getEntityType(), identifier);

    if (result == null) throw new EntityNotFoundException();

    return result;
  }

  protected @NonNull ResponseEntity<@NonNull List<@NonNull Entity>> toResponse (
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

  protected @NonNull JPAEntityCollection<Entity> select (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    @NonNull final RequestConfiguration        configuration = getRequestConfiguration();
    @NonNull final JPAEntityCollection<Entity> collection    = getCollection();

    @NonNull final APIRequest           apiRequest = new APIRequest(request.getParameterMap());
    @NonNull final APIRequestValidation validation = configuration.validate(apiRequest);

    validation.assertRequestIsValid();

    return (JPAEntityCollection<Entity>) configuration.parse(apiRequest).apply(collection);
  }
}
