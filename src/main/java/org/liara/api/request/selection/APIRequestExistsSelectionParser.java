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
package org.liara.api.request.selection;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.RequestConfiguration;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.collection.operator.Identity;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.request.APIRequest;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.APIRequestValidator;

import javax.persistence.EntityManager;

public class APIRequestExistsSelectionParser
  implements APIRequestParser<Operator>,
             APIRequestValidator
{
  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final String        _field;

  @NonNull
  private final Operator _definition;

  @NonNull
  private final RequestConfiguration _configuration;

  @NonNull
  private final Class<?> _entity;

  public APIRequestExistsSelectionParser (
    @NonNull final EntityManager entityManager,
    @NonNull final String field,
    @NonNull final Operator definition, @NonNull final RequestConfiguration configuration,
    @NonNull final Class<?> entity
  ) {
    _entityManager = entityManager;
    _field = field;
    _definition = definition;
    _configuration = configuration;
    _entity = entity;
  }

  @Override
  public @NonNull Operator parse (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.getRequest(_field);

    if (subRequest.getSize() > 0) {
      @NonNull final JPAEntityCollection collection = (JPAEntityCollection) _definition.apply(_configuration.parse(
        request).apply(new JPAEntityCollection<>(_entityManager, _entity))
      );

      return Filter.exists(collection);
    } else {
      return Identity.INSTANCE;
    }
  }

  @Override
  public @NonNull APIRequestValidation validate (@NonNull final APIRequest request) {
    final APIRequest subRequest = request.getRequest(_field);

    return (subRequest.getSize() > 0) ? _configuration.validate(subRequest) : new APIRequestValidation(subRequest);
  }
}
