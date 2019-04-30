package org.liara.api.resource;

import com.fasterxml.jackson.databind.node.NullNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.collection.Collection;
import org.liara.collection.jpa.JPACollections;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.aggregate.AggregableCollection;
import org.liara.collection.operator.grouping.GroupableCollection;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.liara.rest.error.IllegalRestRequestException;
import org.liara.rest.metamodel.RestResource;
import org.liara.rest.processor.ProcessorHandler;
import org.liara.rest.request.RestRequest;
import org.liara.rest.request.handler.RestRequestHandler;
import org.liara.rest.response.RestResponse;
import org.liara.selection.processor.ProcessorExecutor;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.*;

public class AggregationResource<Entity extends ApplicationEntity>
  implements RestResource
{
  @NonNull
  private final CollectionResource<Entity> _collection;

  @NonNull
  private final RelationBasedFilteringHandlerFactory _entityFilteringHandlerFactory;

  @NonNull
  private final RelationBasedAggregationProcessorFactory _entityAggregationHandlerFactory;

  @NonNull
  private final RelationBasedGroupingProcessorFactory _entityGroupingHandlerFactory;

  @NonNull
  private final EntityManager _entityManager;

  public AggregationResource (
    @NonNull final CollectionResource<Entity> collection,
    @NonNull final AggregationResourceBuilder builder
  ) {
    _collection = collection;
    _entityAggregationHandlerFactory =
      Objects.requireNonNull(builder.getEntityAggregationHandlerFactory());
    _entityFilteringHandlerFactory =
      Objects.requireNonNull(builder.getEntityFilteringHandlerFactory());
    _entityGroupingHandlerFactory =
      Objects.requireNonNull(builder.getEntityGroupingHandlerFactory());
    _entityManager = Objects.requireNonNull(builder.getEntityManager());
  }

  @Override
  public @NonNull Mono<RestResponse> get (@NonNull final RestRequest request)
  throws IllegalRestRequestException {
    try {
      @NonNull final RestRequestHandler configuration = getRequestConfiguration();

      configuration.validate(request.getParameters()).assertRequestIsValid();

      @NonNull final Operator      operator   = configuration.parse(request.getParameters());
      @NonNull final Collection<?> collection = operator.apply(getCollection());

      @NonNull final List<@NonNull Tuple> queryResult = getQueryOf(collection).getResultList();

      return Mono.just(render(queryResult, collection));
    } catch (@NonNull final InvalidAPIRequestException exception) {
      throw new IllegalRestRequestException(exception);
    }
  }

  private @NonNull RestResponse render (
    @NonNull final List<@NonNull Tuple> tuples,
    @NonNull final Collection<?> collection
  ) {
    if (tuples.size() <= 0) {
      return RestResponse.ofType(Object.class).ofCollection().build();
    }

    final int groups       = getGroupsOf(collection);
    final int aggregations = getAggregationsOf(collection);

    if (groups <= 0) {
      if (aggregations == 1) {
        if (tuples.get(0).get(0) == null) {
          return RestResponse.ofType(Object.class).ofModel(NullNode.instance);
        } else {
          return RestResponse.ofType(Object.class).ofModel(tuples.get(0).get(0));
        }
      } else {
        return RestResponse.ofType(Object.class).ofCollection().ofModels(
          Arrays.asList(tuples.get(0).toArray())
        ).build();
      }
    }

    @NonNull final List result = new LinkedList();

    for (@NonNull final Tuple tuple : tuples) {
      @NonNull final List entry  = new ArrayList(2);
      @NonNull final List key    = new ArrayList(groups);
      @NonNull final List values = new ArrayList(aggregations);

      for (int index = 0; index < groups; ++index) {
        key.add(tuple.get(index));
      }

      for (int index = groups; index < groups + aggregations; ++index) {
        values.add(tuple.get(index));
      }

      entry.add(key);
      entry.add(values);
      result.add(entry);
    }

    return RestResponse.ofType(Object.class).ofModel(result);
  }

  private int getAggregationsOf (@NonNull final Collection<?> collection) {
    if (collection instanceof AggregableCollection) {
      final int size = ((AggregableCollection<?>) collection).getAggregations().size();
      return size <= 0 ? 1 : size;
    } else {
      return 1;
    }
  }

  private int getGroupsOf (@NonNull final Collection<?> collection) {
    if (collection instanceof GroupableCollection) {
      return ((GroupableCollection<?>) collection).getGroups().size();
    } else {
      return 0;
    }
  }

  private @NonNull TypedQuery<Tuple> getQueryOf (@NonNull final Collection<?> collection) {
    @NonNull final StringBuilder query = new StringBuilder();
    @NonNull final Optional<CharSequence> groupingClause = JPACollections.getGroupingClause(
      collection, "target"
    );
    @NonNull final Optional<CharSequence> aggregations = JPACollections.getAggregations(
      collection, "target"
    );
    @NonNull final Optional<CharSequence> filteringClause = JPACollections.getFilteringClause(
      collection, "target"
    );
    @NonNull final Optional<CharSequence> joinClause = JPACollections.getJoinClause(
      collection, "target"
    );

    query.append("SELECT ");
    if (groupingClause.isPresent()) {
      query.append(groupingClause.get());
      query.append(", ");
    }
    query.append(aggregations.orElse("COUNT(target)"));

    query.append(" FROM ");
    query.append(getModelClass().getName());
    query.append(" target");

    if (joinClause.isPresent()) {
      query.append(" ");
      query.append(joinClause.get());
    }

    if (filteringClause.isPresent()) {
      query.append(" WHERE ");
      query.append(filteringClause.get());
    }

    if (groupingClause.isPresent()) {
      query.append(" GROUP BY ");
      query.append(groupingClause.get());
    }

    @NonNull final TypedQuery<Tuple> result = _entityManager.createQuery(
      query.toString(),
      Tuple.class
    );

    JPACollections.getParameters(collection).forEach(result::setParameter);

    return result;
  }

  protected @NonNull RestRequestHandler getRequestConfiguration () {
    return RestRequestHandler.all(
      getResourceFilteringHandler(),
      RestRequestHandler.parameter("groupby", new ProcessorHandler<>(getResourceGroupingHandler())),
      RestRequestHandler.parameter(
        "fields",
        new ProcessorHandler<>(getResourceAggregationHandler())
      )
    );
  }

  public @NonNull RestRequestHandler getResourceFilteringHandler () {
    return RestRequestHandler.all(
      _entityFilteringHandlerFactory.getHandlerFor(getModelClass())
    );
  }

  public @NonNull ProcessorExecutor<Operator> getResourceGroupingHandler () {
    return _entityGroupingHandlerFactory.getExecutorFor(getModelClass());
  }

  public @NonNull ProcessorExecutor<Operator> getResourceAggregationHandler () {
    return _entityAggregationHandlerFactory.getExecutorFor(getModelClass());
  }

  public @NonNull Collection<Entity> getCollection () {
    return _collection.getCollection();
  }

  public @NonNull Class<Entity> getModelClass () {
    return _collection.getModelClass();
  }

  public @NonNull RelationBasedFilteringHandlerFactory getEntityFilteringHandlerFactory () {
    return _entityFilteringHandlerFactory;
  }

  public @NonNull RelationBasedAggregationProcessorFactory getEntityAggregationHandlerFactory () {
    return _entityAggregationHandlerFactory;
  }

  public @NonNull RelationBasedGroupingProcessorFactory getEntityGroupingHandlerFactory () {
    return _entityGroupingHandlerFactory;
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (other == null) return false;
    if (other == this) return true;

    if (other instanceof AggregationResource) {
      @NonNull final AggregationResource otherAggregationResource = (AggregationResource) other;

      return Objects.equals(
        _collection,
        otherAggregationResource._collection
      ) &&
             Objects.equals(
               _entityFilteringHandlerFactory,
               otherAggregationResource.getEntityFilteringHandlerFactory()
             ) &&
             Objects.equals(
               _entityAggregationHandlerFactory,
               otherAggregationResource.getEntityAggregationHandlerFactory()
             ) &&
             Objects.equals(
               _entityGroupingHandlerFactory,
               otherAggregationResource.getEntityGroupingHandlerFactory()
             ) &&
             Objects.equals(
               _entityManager,
               otherAggregationResource._entityManager
             );
    }

    return false;
  }

  @Override
  public int hashCode () {
    return Objects.hash(
      _collection,
      _entityFilteringHandlerFactory,
      _entityAggregationHandlerFactory,
      _entityGroupingHandlerFactory,
      _entityManager
    );
  }
}
