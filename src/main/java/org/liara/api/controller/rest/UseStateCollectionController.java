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
package org.liara.api.controller.rest;

import java.time.Duration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.collection.transformation.MapValueTransformation;
import org.liara.api.collection.transformation.aggregation.EntityCountAggregationTransformation;
import org.liara.api.collection.transformation.aggregation.ExpressionAggregationTransformation;
import org.liara.api.data.collection.ActivationStateCollection;
import org.liara.api.data.collection.configuration.ActivationStateCollectionRequestConfiguration;
import org.liara.api.data.entity.state.ActivationState;
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
      "states<use>"
    },
    description = "",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public class UseStateCollectionController extends BaseRestController
{  
  @Autowired
  private ActivationStateCollection _collection;
  
  @GetMapping("/states<use>")
  @ParametersFromConfiguration(
    value = ActivationStateCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<ActivationState>> index (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return indexCollection(_collection.uses(), request);
  }
  
  @GetMapping("/states<use>/{identifier}")
  public ActivationState index (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException
  {
    return _collection.uses().findByIdentifierOrFail(identifier);
  }
  

  @GetMapping("/states<use>/count")
  @ParametersFromConfiguration(
    value = ActivationStateCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> count (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(
      _collection.uses(), request, 
      EntityCountAggregationTransformation.create()
    );
  }
  
  @GetMapping("/states<use>/sum")
  @ParametersFromConfiguration(
    value = ActivationStateCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> sum (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(
      _collection.uses(), request, 
      new ExpressionAggregationTransformation<>(
        (query, entity) -> {
          return query.getManager().getCriteriaBuilder().sum(
            ActivationState.DURATION_SELECTOR.select(query, entity)
          );
        }, Long.class
      ), 
      new MapValueTransformation<>(Duration::ofMillis)
    );
  }
  
  @GetMapping("/states<use>/avg")
  @ParametersFromConfiguration(
    value = ActivationStateCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> avg (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException
  {
    return aggregate(
      _collection.uses(), request, 
      new ExpressionAggregationTransformation<>(
        (query, entity) -> {
          return query.getManager().getCriteriaBuilder().avg(
            ActivationState.DURATION_SELECTOR.select(query, entity)
          );
        }, Double.class
      ), 
      new MapValueTransformation<>((x) -> Duration.ofMillis(x.longValue()))
    );
  }
}
