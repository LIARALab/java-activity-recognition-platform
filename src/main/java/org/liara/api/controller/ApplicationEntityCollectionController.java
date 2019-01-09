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

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.collection.controller.*;
import org.liara.api.collection.configuration.RequestConfiguration;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.metamodel.collection.CollectionController;
import org.liara.api.metamodel.collection.CountableCollectionController;
import org.liara.api.metamodel.collection.GetableCollectionController;
import org.liara.api.metamodel.collection.IndexableCollectionController;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ApplicationEntityCollectionController<Entity extends ApplicationEntity>
  implements CollectionController<Entity>,
             IndexableCollectionController<Entity>,
             CountableCollectionController<Entity>,
             GetableCollectionController<Entity>
{
  @NonNull
  private final RestCollectionControllerConfiguration _configuration;

  @NonNull
  private final Class<Entity> _modelClass;

  @Autowired
  public ApplicationEntityCollectionController (
    @NonNull final RestCollectionControllerConfiguration configuration, @NonNull final Class<Entity> modelClass
  )
  {
    _configuration = configuration;
    _modelClass = modelClass;
  }

  @Override
  public @NonNull JPAEntityCollection<Entity> index (
    @NonNull final APIRequest request
  )
  throws InvalidAPIRequestException
  {
    return select(request);
  }

  @Override
  public @NonNegative @NonNull Long count (
    @NonNull final APIRequest request
  )
  throws InvalidAPIRequestException
  {
    return select(request).select("COUNT(:this)", Long.class).getSingleResult();
  }

  @Override
  public @NonNull Entity get (@NonNull final Long identifier)
  throws EntityNotFoundException
  {
    @Nullable final Entity entity = _configuration.getEntityManager().find(getModelClass(), identifier);

    if (entity == null) {
      throw new EntityNotFoundException();
    } else {
      return entity;
    }
  }

  @Override
  public @NonNull Entity get (@NonNull final UUID identifier)
  throws EntityNotFoundException
  {
    @NonNull final List<Entity> result = _configuration.getEntityManager()
                                           .createQuery("SELECT entity FROM " + getModelClass().getName() +
                                                        " entity WHERE entity.UUID = :uuid",
                                             getModelClass()
                                           )
                                           .setParameter("uuid", identifier)
                                           .getResultList();

    if (result.size() > 0) {
      return result.get(0);
    } else {
      throw new EntityNotFoundException();
    }
  }

  @Override
  public @NonNull ModelController<Entity> getModelController () {
    return new ApplicationModelController<>(_modelClass);
  }

  private @NonNull JPAEntityCollection<Entity> select (@NonNull final APIRequest request)
  throws InvalidAPIRequestException
  {
    @NonNull final RequestConfiguration configuration = getRequestConfiguration();

    configuration.validate(request).assertRequestIsValid();

    return (JPAEntityCollection<Entity>) configuration.parse(request).apply(getCollection());
  }

  public @NonNull RequestConfiguration getRequestConfiguration () {
    return Objects.requireNonNull(_configuration.getEntityConfigurationFactory())
             .create(getModelClass());
  }

  public @NonNull JPAEntityCollection<Entity> getCollection () {
    return new JPAEntityCollection<>(Objects.requireNonNull(_configuration.getEntityManager()), getModelClass());
  }

  public @NonNull RestCollectionControllerConfiguration getConfiguration () {
    return _configuration;
  }

  @Override
  public @NonNull Class<Entity> getModelClass () {
    return _modelClass;
  }
}
