package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.relation.Relation;
import org.liara.api.relation.RelationManager;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.rest.request.handler.RestRequestHandler;
import org.liara.rest.request.jpa.EntityFilteringHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.ManagedType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class RelationBasedFilteringHandlerFactory
{
  @NonNull
  private final static Map<@NonNull Class<?>, @NonNull RestRequestHandler> CONFIGURATIONS =
    new HashMap<>();

  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final RelationManager _relationManager;

  @NonNull
  private final EntityFilteringHandlerFactory _entityFilteringHandlerFactory;

  @Autowired
  public RelationBasedFilteringHandlerFactory (
    @NonNull final RelationManager relationManager,
    @NonNull final EntityFilteringHandlerFactory entityFilteringHandlerFactory,
    @NonNull final EntityManager entityManager
  ) {
    _entityManager = entityManager;
    _relationManager = relationManager;
    _entityFilteringHandlerFactory = entityFilteringHandlerFactory;
  }

  public @NonNull RestRequestHandler getHandlerFor (@NonNull final Class<?> clazz) {
    if (!CONFIGURATIONS.containsKey(clazz)) {
      CONFIGURATIONS.put(clazz, createHandler(clazz));
    }

    return CONFIGURATIONS.get(clazz);
  }

  public @NonNull RestRequestHandler getHandlerFor (@NonNull final ManagedType<?> entity) {
    return getHandlerFor(entity.getJavaType());
  }

  private @NonNull RestRequestHandler createHandler (
    @NonNull final Class<?> clazz
  ) {
    @NonNull final List<@NonNull RestRequestHandler> handlers = new LinkedList<>();
    @NonNull
    final Map<@NonNull String, @NonNull Relation> relations = _relationManager.getRelationsOf(
      clazz
    );

    handlers.add(_entityFilteringHandlerFactory.getHandlerFor(clazz));

    for (@NonNull final String name : relations.keySet()) {
      handlers.add(
        RestRequestHandler.subRequest(
          name,
          RestRequestHandler.factory(getHandlerFactoryFor(relations.get(name)))
        )
      );
    }

    return RestRequestHandler.all(handlers);
  }

  private @NonNull Supplier<RestRequestHandler> getHandlerFactoryFor (
    @NonNull final Relation relation
  ) {
    if (relation.isCollectionRelation()) {
      return getCollectionHandlerFactory(relation);
    } else {
      return getModelHandlerFactory(relation);
    }
  }

  private @NonNull Supplier<RestRequestHandler> getModelHandlerFactory (
    @NonNull final Relation relation
  ) {
    return () -> getHandlerFor(relation.getDestinationClass()).map(relation.getOperator()::apply);
  }

  private @NonNull Supplier<RestRequestHandler> getCollectionHandlerFactory (
    @NonNull final Relation relation
  ) {
    return () -> getHandlerFor(relation.getDestinationClass()).map(
      (@NonNull final Operator operator) -> Filter.exists(
        (JPAEntityCollection) operator.apply(relation.getOperator().apply(
          new JPAEntityCollection(_entityManager, relation.getDestinationClass())
        ))
      )
    );
  }
}
