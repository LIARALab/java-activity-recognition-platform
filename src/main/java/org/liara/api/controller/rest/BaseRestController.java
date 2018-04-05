/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.EntityCollection;
import org.liara.api.collection.EntityCollectionView;
import org.liara.api.collection.EntityCollections;
import org.liara.api.data.entity.filters.EntityFilterFactory;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIRequestFreeCursorParser;
import org.liara.api.request.validator.APIRequestValidator;
import org.liara.api.request.validator.APIRequestFreeCursorValidator;
import org.liara.api.request.validator.error.APIRequestError;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public class BaseRestController
{
  @Autowired
  @NonNull
  private EntityCollections _collections;

  public <Entity> ResponseEntity<List<Entity>> indexCollection (
    @NonNull final Class<Entity> entity,
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException
  {
    final EntityCollection<Entity, Long> collection = _collections.createCollection(entity);
    final APIRequest apiRequest = APIRequest.from(request);
    
    this.assertIsValidRequest(apiRequest, new APIRequestFreeCursorValidator());

    final EntityCollectionView<Entity> view = collection.getView((new APIRequestFreeCursorParser()).parse(apiRequest));

    if (view.getSize() == collection.getSize()) {
      return new ResponseEntity<>(view.getContent(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(view.getContent(), HttpStatus.PARTIAL_CONTENT);
    }
  }

  public <Entity> ResponseEntity<List<Entity>> indexCollection (
    @NonNull final Class<Entity> entity,
    @NonNull final EntityFilterFactory<Entity> filter,
    @NonNull final HttpServletRequest request
  )
    throws InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);
    final List<APIRequestValidator> validators = new ArrayList<>();
    validators.addAll(filter.createValidators());
    validators.add(new APIRequestFreeCursorValidator());

    this.assertIsValidRequest(apiRequest, validators);

    final EntityCollection<Entity, Long> collection = _collections.createCollection(
      entity, filter.createFilterParser().parse(apiRequest)
    );

    final EntityCollectionView<Entity> view = collection.getView((new APIRequestFreeCursorParser()).parse(apiRequest));

    if (view.getSize() == collection.getSize()) {
      return new ResponseEntity<>(view.getContent(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(view.getContent(), HttpStatus.PARTIAL_CONTENT);
    }
  }

  public <Entity> long countCollection (@NonNull final Class<Entity> entity, @NonNull final HttpServletRequest request)
    throws InvalidAPIRequestException
  {
    final EntityCollection<Entity, Long> collection = _collections.createCollection(entity);
    return collection.getSize();
  }

  public <Entity> long countCollection (
    @NonNull final Class<Entity> entity,
    @NonNull final EntityFilterFactory<Entity> filter,
    @NonNull final HttpServletRequest request
  )
    throws InvalidAPIRequestException
  {
    final APIRequest apiRequest = APIRequest.from(request);

    this.assertIsValidRequest(apiRequest, filter.createValidators());

    final EntityCollection<Entity, Long> collection = _collections.createCollection(
      entity, filter.createFilterParser().parse(apiRequest)
    );

    return collection.getSize();
  }

  public void assertIsValidRequest (
    @NonNull final APIRequest request, 
    @NonNull final APIRequestValidator... validators
  ) throws InvalidAPIRequestException
  {
    this.assertIsValidRequest(request, Arrays.asList(validators));
  }
  
  public void assertIsValidRequest (
    @NonNull final APIRequest request, 
    @NonNull final List<APIRequestValidator> validators
  ) throws InvalidAPIRequestException
  {
    final List<APIRequestError> errors = new ArrayList<>();

    for (final APIRequestValidator validator : validators) {
      errors.addAll(validator.validate(request));
    }

    if (errors.size() > 0) {
      throw new InvalidAPIRequestException(request, errors);
    }
  }
}
