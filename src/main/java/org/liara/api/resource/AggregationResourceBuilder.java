package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class AggregationResourceBuilder
{
  @Nullable
  private RelationBasedFilteringHandlerFactory _entityFilteringHandlerFactory;

  @Nullable
  private RelationBasedAggregationProcessorFactory _entityAggregationHandlerFactory;

  @Nullable
  private RelationBasedGroupingProcessorFactory _entityGroupingHandlerFactory;

  @Nullable
  private EntityManager _entityManager;

  public AggregationResourceBuilder () {
    _entityAggregationHandlerFactory = null;
    _entityFilteringHandlerFactory = null;
    _entityGroupingHandlerFactory = null;
    _entityManager = null;
  }

  public AggregationResourceBuilder (@NonNull final AggregationResourceBuilder toCopy) {
    _entityGroupingHandlerFactory = toCopy.getEntityGroupingHandlerFactory();
    _entityFilteringHandlerFactory = toCopy.getEntityFilteringHandlerFactory();
    _entityAggregationHandlerFactory = toCopy.getEntityAggregationHandlerFactory();
    _entityManager = toCopy.getEntityManager();
  }

  public @Nullable EntityManager getEntityManager () {
    return _entityManager;
  }

  @Autowired
  public void setEntityManager (@Nullable final EntityManager entityManager) {
    _entityManager = entityManager;
  }

  public <Entity extends ApplicationEntity> @NonNull AggregationResource<Entity> build (
    @NonNull final CollectionResource<Entity> entity
  ) {
    return new AggregationResource<>(entity, this);
  }

  public @Nullable RelationBasedFilteringHandlerFactory getEntityFilteringHandlerFactory () {
    return _entityFilteringHandlerFactory;
  }

  @Autowired
  public void setEntityFilteringHandlerFactory (
    @Nullable final RelationBasedFilteringHandlerFactory entityFilteringHandlerFactory
  ) {
    _entityFilteringHandlerFactory = entityFilteringHandlerFactory;
  }

  public @Nullable RelationBasedAggregationProcessorFactory getEntityAggregationHandlerFactory () {
    return _entityAggregationHandlerFactory;
  }

  @Autowired
  public void setEntityAggregationHandlerFactory (
    @Nullable final RelationBasedAggregationProcessorFactory entityAggregationHandlerFactory
  ) {
    _entityAggregationHandlerFactory = entityAggregationHandlerFactory;
  }

  public @Nullable RelationBasedGroupingProcessorFactory getEntityGroupingHandlerFactory () {
    return _entityGroupingHandlerFactory;
  }

  @Autowired
  public void setEntityGroupingHandlerFactory (
    @Nullable final RelationBasedGroupingProcessorFactory entityGroupingHandlerFactory
  ) {
    _entityGroupingHandlerFactory = entityGroupingHandlerFactory;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof AggregationResourceBuilder) {
      @NonNull final AggregationResourceBuilder otherAggregationResourceBuilder =
        (AggregationResourceBuilder) other;

      return Objects.equals(
        _entityFilteringHandlerFactory,
        otherAggregationResourceBuilder.getEntityFilteringHandlerFactory()
      ) &&
             Objects.equals(
               _entityAggregationHandlerFactory,
               otherAggregationResourceBuilder.getEntityAggregationHandlerFactory()
             ) &&
             Objects.equals(
               _entityGroupingHandlerFactory,
               otherAggregationResourceBuilder.getEntityGroupingHandlerFactory()
             ) &&
             Objects.equals(
               _entityManager,
               otherAggregationResourceBuilder.getEntityManager()
             );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _entityFilteringHandlerFactory,
      _entityAggregationHandlerFactory,
      _entityGroupingHandlerFactory,
      _entityManager
    );
  }
}
