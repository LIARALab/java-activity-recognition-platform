package org.liara.api.controller.rest;

import io.swagger.annotations.Api;
import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.collection.transformation.MapValueTransformation;
import org.liara.api.collection.transformation.aggregation.EntityCountAggregationTransformation;
import org.liara.api.collection.transformation.aggregation.ExpressionAggregationTransformation;
import org.liara.api.data.collection.LabelStateCollection;
import org.liara.api.data.collection.configuration.LabelStateCollectionRequestConfiguration;
import org.liara.api.data.entity.state.LabelState;
import org.liara.api.documentation.ParametersFromConfiguration;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.List;

@RestController
@Api(
    tags = {
      "states<labels>"
    },
    description = "All activities-related operation.",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public class LabelCollectionController
  extends BaseRestController
{
  @Autowired
  @NonNull
  private LabelStateCollection _collection;

  @GetMapping("/states<label>/count")
  @ParametersFromConfiguration(value = LabelStateCollectionRequestConfiguration.class,
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

  @GetMapping("/states<label>")
  @ParametersFromConfiguration(value = LabelStateCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<LabelState>> index (
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException {
    return indexCollection(_collection, request);
  }

  @GetMapping("/states<label>/{identifier}")
  public LabelState get (
    @PathVariable final long identifier
  ) throws EntityNotFoundException {
    return _collection.findByIdentifierOrFail(identifier);
  }


  @GetMapping("/states<label>/sum")
  @ParametersFromConfiguration(value = LabelStateCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> sum (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(
      _collection, request, 
      new ExpressionAggregationTransformation<>(
        (query, entity) -> {
          return query.getManager().getCriteriaBuilder().sum(LabelState.DURATION_SELECTOR.select(query, entity)
          );
        }, Long.class
      ), 
      new MapValueTransformation<>(Duration::ofMillis)
    );
  }

  @GetMapping("/states<label>/avg")
  @ParametersFromConfiguration(value = LabelStateCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> avg (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(
      _collection, request, 
      new ExpressionAggregationTransformation<>(
        (query, entity) -> {
          return query.getManager().getCriteriaBuilder().avg(LabelState.DURATION_SELECTOR.select(query, entity)
          );
        }, Double.class
      ), 
      new MapValueTransformation<>((x) -> Duration.ofMillis(x.longValue()))
    );
  }
}
