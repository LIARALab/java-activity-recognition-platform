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
package org.liara.api.controller;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.liara.rest.metamodel.collection.GetableCollection;
import org.liara.rest.metamodel.collection.RestCollection;
import org.liara.rest.metamodel.model.RestModel;
import org.liara.rest.request.RestRequestHandler;

import java.util.Objects;

public class ApplicationEntityCollectionController<Entity extends ApplicationEntity>
  implements RestCollection<Entity>,
             GetableCollection<Entity>
{
  @NonNull
  private final Class<Entity> _modelClass;

  @NonNull
  private final ReadableControllerConfiguration _configuration;

  public ApplicationEntityCollectionController (
    @NonNull final Class<Entity> modelClass, @NonNull final ReadableControllerConfiguration configuration
  )
  {
    _configuration = configuration;
    _modelClass = modelClass;
  }

  @Override
  public @NonNull JPAEntityCollection<Entity> get (
    @NonNull final APIRequest request
  )
  throws InvalidAPIRequestException
  {
    @NonNull final RestRequestHandler configuration = getRequestConfiguration();

    configuration.validate(request).assertRequestIsValid();

    return (JPAEntityCollection<Entity>) configuration.parse(request).apply(getCollection());
  }

  public @NonNull RestRequestHandler getRequestConfiguration () {
    return _configuration.getEntityConfigurationFactory().create(getModelClass());
  }

  public @NonNull JPAEntityCollection<Entity> getCollection () {
    return new JPAEntityCollection<>(Objects.requireNonNull(_configuration.getEntityManager()), getModelClass());
  }

  @Override
  public @NonNull RestModel<Entity> getModelController () {
    return new ApplicationModelController<>(_modelClass, _configuration);
  }

  @Override
  public @NonNull Class<Entity> getModelClass () {
    return _modelClass;
  }
}
