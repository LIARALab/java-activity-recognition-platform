package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.relation.Relation;
import org.liara.api.relation.RelationManager;
import org.liara.collection.operator.Operator;
import org.liara.rest.processor.ProcessorHandler;
import org.liara.rest.request.handler.RestParameterHandler;
import org.liara.rest.request.jpa.EntityAggregationHandlerFactory;
import org.liara.selection.processor.ProcessorExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.ManagedType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class RelationBasedAggregationProcessorFactory
{
  @NonNull
  private final static Map<@NonNull Class<?>, @NonNull ProcessorExecutor<Operator>> CONFIGURATIONS =
    new HashMap<>();

  @NonNull
  private final RelationManager _relationManager;

  @NonNull
  private final EntityAggregationHandlerFactory _entityAggregationHandlerFactory;

  @Autowired
  public RelationBasedAggregationProcessorFactory (
    @NonNull final RelationManager relationManager,
    @NonNull final EntityAggregationHandlerFactory entityAggregationHandlerFactory
  ) {
    _relationManager = relationManager;
    _entityAggregationHandlerFactory = entityAggregationHandlerFactory;
  }

  public @NonNull RestParameterHandler getHandlerFor (
    @NonNull final Class<?> entity
  ) {
    return new ProcessorHandler<>(getExecutorFor(entity));
  }

  public @NonNull RestParameterHandler getHandlerFor (
    @NonNull final ManagedType<?> entity
  ) {
    return getHandlerFor(entity.getJavaType());
  }

  public @NonNull ProcessorExecutor<Operator> getExecutorFor (@NonNull final Class<?> clazz) {
    if (!CONFIGURATIONS.containsKey(clazz)) {
      CONFIGURATIONS.put(clazz, createExecutor(clazz));
    }

    return CONFIGURATIONS.get(clazz);
  }

  public @NonNull ProcessorExecutor<Operator> getExecutorFor (
    @NonNull final ManagedType<?> entity
  ) {
    return getExecutorFor(entity.getJavaType());
  }

  private @NonNull ProcessorExecutor<Operator> createExecutor (
    @NonNull final Class<?> clazz
  ) {
    @NonNull final List<@NonNull ProcessorExecutor<Operator>> handlers = new LinkedList<>();
    @NonNull
    final Map<@NonNull String, @NonNull Relation> relations = _relationManager.getRelationsOf(
      clazz
    );

    handlers.add(_entityAggregationHandlerFactory.getExecutorFor(clazz));

    for (@NonNull final String name : relations.keySet()) {
      handlers.add(
        ProcessorExecutor.field(
          name,
          ProcessorExecutor.factory(getHandlerFactoryFor(relations.get(name)))
        )
      );
    }

    return ProcessorExecutor.all(handlers);
  }

  private @NonNull Supplier<ProcessorExecutor<Operator>> getHandlerFactoryFor (
    @NonNull final Relation relation
  ) {
    return getModelHandlerFactory(relation);
  }

  private @NonNull Supplier<ProcessorExecutor<Operator>> getModelHandlerFactory (
    @NonNull final Relation relation
  ) {
    return () -> getExecutorFor(relation.getDestinationClass()).mapNonNull(
      relation.getOperator()::apply
    );
  }

  /*
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
  */
}
