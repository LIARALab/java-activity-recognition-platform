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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.collection.transformation.aggregation.EntityCountAggregationTransformation;
import org.liara.api.data.collection.StateCollection;
import org.liara.api.data.collection.configuration.StateCollectionRequestConfiguration;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateCreationSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.documentation.ParametersFromConfiguration;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.lang.NonNull;

import io.swagger.annotations.Api;

@RestController
@Api(
    tags = {
      "state"
    },
    description = "All sensor-related operation.",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public class StateCollectionController extends BaseRestController
{
  @Autowired
  @NonNull
  private SchemaManager _schemaManager;
  
  @Autowired
  @NonNull
  private StateCollection _collection;

  @GetMapping("/states/count")
  @ParametersFromConfiguration(
    value = StateCollectionRequestConfiguration.class,
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

  @GetMapping("/states")
  @ParametersFromConfiguration(
    value = StateCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<State>> index (
    @NonNull final HttpServletRequest request
  ) throws InvalidAPIRequestException {
    return indexCollection(_collection, request);
  }
  
  @PostMapping("/states")
  @Transactional
  public ResponseEntity<Void> create (
    @NonNull final HttpServletRequest request,
    @NonNull @Valid @RequestBody final StateCreationSchema schema
  ) {
    final State state = _schemaManager.execute(schema);
    
    final HttpHeaders headers = new HttpHeaders();
    headers.add("Location", request.getRequestURI() + "/" + state.getIdentifier());
    
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping("/states/{identifier}")
  public State get (
    @PathVariable final long identifier
  ) throws EntityNotFoundException {
    return _collection.findByIdentifierOrFail(identifier);
  }
  
  @GetMapping("/states/{identifier}/sensor")
  public Sensor getSensor (
    @PathVariable final long identifier
  ) throws EntityNotFoundException {
    return _collection.findByIdentifierOrFail(identifier).getSensor();
  }
}
