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
package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.utils.Builder;
import org.liara.api.utils.Duplicator;
import org.liara.collection.Collection;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.collection.operator.Operator;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.liara.rest.cursor.FreeCursorHandler;
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.metamodel.FilterableRestResource;
import org.liara.rest.metamodel.OrderableRestResource;
import org.liara.rest.metamodel.RestResource;
import org.liara.rest.processor.ProcessorHandler;
import org.liara.rest.request.RestRequest;
import org.liara.rest.request.handler.RestRequestHandler;
import org.liara.rest.response.RestResponse;
import org.liara.selection.processor.ProcessorExecutor;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CollectionResource<Entity extends ApplicationEntity>
  implements RestResource, FilterableRestResource, OrderableRestResource
{
  @NonNull
  private static final Predicate<String> IS_LONG = Pattern.compile("^[0-9]+$").asPredicate();

  @NonNull
  private static final Predicate<String> IS_UUID4 = Pattern.compile(
    "^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$",
    Pattern.CASE_INSENSITIVE
  ).asPredicate();

  @NonNull
  private final Class<Entity> _modelClass;

  @NonNull
  private final RelationBasedFilteringHandlerFactory _entityFilteringHandlerFactory;

  @NonNull
  private final RelationBasedOrderingProcessorFactory _entityOrderingHandlerFactory;

  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final CollectionResourceBuilder _builder;

  protected CollectionResource (
    @NonNull final Class<Entity> modelClass,
    @NonNull final CollectionResourceBuilder builder
  ) {
    _modelClass = modelClass;
    _entityOrderingHandlerFactory = Builder.require(builder.getEntityOrderingHandlerFactory());
    _entityFilteringHandlerFactory = Builder.require(builder.getEntityFilteringHandlerFactory());
    _entityManager = Builder.require(builder.getEntityManager());
    _builder = Duplicator.duplicate(builder);
  }

  @Override
  public boolean hasResource (@NonNull final String name) {
    return IS_UUID4.test(name) ||
           IS_LONG.test(name) ||
           "first".equalsIgnoreCase(name) ||
           "last".equalsIgnoreCase(name)
      ;
  }

  @Override
  public @NonNull RestResource getResource (@NonNull final String name)
  throws NoSuchElementException {
    if (IS_UUID4.test(name)) {
      return getModelResource(UUID.fromString(name));
    }

    if (IS_LONG.test(name)) {
      return getModelResource(Long.parseLong(name));
    }

    if ("first".equalsIgnoreCase(name)) {
      return getFirstModelResource();
    }

    if ("last".equalsIgnoreCase(name)) {
      return getLastModelResource();
    }

    return FilterableRestResource.super.getResource(name);
  }

  public @NonNull ModelResource<Entity> getFirstModelResource ()
  throws NoSuchElementException {
    @NonNull final List<@NonNull Entity> identifiers = _entityManager.createQuery(
      "SELECT model " +
      "FROM " + _modelClass.getName() + " model " +
      "ORDER BY model.identifier ASC",
      getModelClass()
    ).setMaxResults(1).getResultList();

    if (identifiers.isEmpty()) {
      throw new NoSuchElementException("No first model found for this collection.");
    } else {
      return toModelResource(identifiers.get(0));
    }
  }

  public @NonNull ModelResource<Entity> getLastModelResource ()
  throws NoSuchElementException {
    @NonNull final List<@NonNull Entity> identifiers = _entityManager.createQuery(
      "SELECT model " +
      "FROM " + _modelClass.getName() + " model " +
      "ORDER BY model.identifier DESC",
      getModelClass()
    ).setMaxResults(1).getResultList();

    if (identifiers.isEmpty()) {
      throw new NoSuchElementException("No last model found for this collection.");
    } else {
      return toModelResource(identifiers.get(0));
    }
  }

  public @NonNull ModelResource<Entity> getModelResource (@NonNull final UUID identifier)
  throws NoSuchElementException {
    @NonNull final List<@NonNull Entity> identifiers = _entityManager.createQuery(
      "SELECT model " +
      "FROM " + _modelClass.getName() + " model " +
      "WHERE model.universalUniqueIdentifier = :identifier " +
      "ORDER BY model.identifier DESC",
      getModelClass()
    ).setParameter("identifier", identifier.toString())
                                                         .setMaxResults(1).getResultList();

    if (identifiers.isEmpty()) {
      throw new NoSuchElementException(
        "No model with uuid " + identifier.toString() + " found into this collection."
      );
    } else {
      return toModelResource(identifiers.get(0));
    }
  }

  public @NonNull ModelResource<Entity> getModelResource (
    @NonNull final Long identifier
  )
  throws NoSuchElementException {
    try {
      return toModelResource(_entityManager.find(_modelClass, identifier));
    } catch (@NonNull final EntityNotFoundException exception) {
      throw new NoSuchElementException(
        "No model with identifier " + identifier.toString() + " found into this collection."
      );
    }
  }

  protected @NonNull ModelResource<Entity> toModelResource (@NonNull final Entity entity) {
    @NonNull final BaseModelResourceBuilder<Entity> builder = new BaseModelResourceBuilder<>();
    builder.setModelClass(getModelClass());
    builder.setModel(entity);
    builder.setRelationManager(_builder.getRelationManager());
    builder.setCollectionResourceBuilder(_builder);

    return builder.build();
  }

  @Override
  public @NonNull Mono<RestResponse> get (@NonNull final RestRequest request)
  throws IllegalRestRequestException {
    try {
      @NonNull final RestRequestHandler configuration = getRequestConfiguration();

      configuration.validate(request.getParameters()).assertRequestIsValid();

      System.out.println((
        (JPAEntityCollection) configuration.parse(request.getParameters())
                                .apply(getCollection())
      ).getQuery(":this"));

      return Mono.just(
        RestResponse.ofCollection(
          configuration.parse(request.getParameters()).apply(getCollection())
        )
      );
    } catch (@NonNull final InvalidAPIRequestException exception) {
      throw new IllegalRestRequestException(exception);
    }
  }

  protected @NonNull RestRequestHandler getRequestConfiguration () {
    return RestRequestHandler.all(
      FreeCursorHandler.INSTANCE,
      getResourceFilteringHandler(),
      RestRequestHandler.parameter("orderby", new ProcessorHandler<>(getResourceOrderingHandler()))
    );
  }

  public @NonNull Collection<Entity> getCollection () {
    return new JPAEntityCollection<>(_entityManager, getModelClass());
  }

  public @NonNull Class<Entity> getModelClass () {
    return _modelClass;
  }

  @Override
  public @NonNull RestRequestHandler getResourceFilteringHandler () {
    return RestRequestHandler.all(
      _entityFilteringHandlerFactory.getHandlerFor(_modelClass)
    );
  }

  @Override
  public @NonNull ProcessorExecutor<Operator> getResourceOrderingHandler () {
    return _entityOrderingHandlerFactory.getExecutorFor(_modelClass);
  }

  public @NonNull RelationBasedFilteringHandlerFactory getEntityFilteringHandlerFactory () {
    return _entityFilteringHandlerFactory;
  }

  public @NonNull RelationBasedOrderingProcessorFactory getEntityOrderingHandlerFactory () {
    return _entityOrderingHandlerFactory;
  }

  public @NonNull EntityManager getEntityManager () {
    return _entityManager;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof CollectionResource) {
      @NonNull final CollectionResource otherCollectionResource = (CollectionResource) other;

      return Objects.equals(_modelClass, otherCollectionResource.getModelClass()) &&
             Objects.equals(
               _entityFilteringHandlerFactory,
               otherCollectionResource.getEntityFilteringHandlerFactory()
             ) &&
             Objects.equals(
               _entityOrderingHandlerFactory,
               otherCollectionResource.getEntityOrderingHandlerFactory()
             ) &&
             Objects.equals(_entityManager, otherCollectionResource.getEntityManager());
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _modelClass,
      _entityFilteringHandlerFactory,
      _entityOrderingHandlerFactory,
      _entityManager
    );
  }
}
