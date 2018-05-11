package org.liara.api.controller.rest;

import java.time.Duration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.collection.transformation.MapValueTransformation;
import org.liara.api.collection.transformation.aggregation.EntityCountAggregationTransformation;
import org.liara.api.collection.transformation.aggregation.ExpressionAggregationTransformation;
import org.liara.api.data.collection.ActivityStateCollection;
import org.liara.api.data.collection.configuration.ActivityStateCollectionRequestConfiguration;
import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.documentation.ParametersFromConfiguration;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(
    tags = {
      "activities"
    },
    description = "All activities-related operation.",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public class ActivitiesCollectionController extends BaseRestController
{
  @Autowired
  @NonNull
  private ActivityStateCollection _collection;
  
  @GetMapping("/activities/count")
  @ParametersFromConfiguration(
    value = ActivityStateCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> count (
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException {
    return aggregate(
      _collection, request, 
      EntityCountAggregationTransformation.create()
    );
  }

  @GetMapping("/activities")
  @ParametersFromConfiguration(
    value = ActivityStateCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<ActivityState>> index (
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException {
    return indexCollection(_collection, request);
  }

  @GetMapping("/activities/{identifier}")
  public ActivityState get (
    @PathVariable final long identifier
  ) throws EntityNotFoundException {
    return _collection.findByIdentifierOrFail(identifier);
  }
  

  @GetMapping("/activities/sum")
  @ParametersFromConfiguration(
    value = ActivityStateCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> sum (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(
      _collection, request, 
      new ExpressionAggregationTransformation<>(
        (query, entity) -> {
          return query.getManager().getCriteriaBuilder().sum(
            ActivityState.DURATION_SELECTOR.select(query, entity)
          );
        }, Long.class
      ), 
      new MapValueTransformation<>(Duration::ofMillis)
    );
  }
  
  @GetMapping("/activities/avg")
  @ParametersFromConfiguration(
    value = ActivityStateCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> avg (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(
      _collection, request, 
      new ExpressionAggregationTransformation<>(
        (query, entity) -> {
          return query.getManager().getCriteriaBuilder().avg(
            ActivityState.DURATION_SELECTOR.select(query, entity)
          );
        }, Double.class
      ), 
      new MapValueTransformation<>((x) -> Duration.ofMillis(x.longValue()))
    );
  }
}
