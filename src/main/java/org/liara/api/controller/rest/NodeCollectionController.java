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

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.collection.transformation.aggregation.EntityCountAggregationTransformation;
import org.liara.api.data.collection.configuration.NodeCollectionRequestConfiguration;
import org.liara.api.data.collection.configuration.SensorCollectionRequestConfiguration;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.node.NodeModifier;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.documentation.ParametersFromConfiguration;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.liara.api.validation.RestGroup;
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
import org.springframework.validation.annotation.Validated;

import io.swagger.annotations.Api;

@RestController
@Api(
    tags = {
      "node"
    },
    description = "",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
/**
 * A controller for all API endpoints that read, write or update information about nodes.
 * 
 * @author Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public final class NodeCollectionController extends BaseRestController
{
  @Autowired
  @NonNull
  private NodeCollection _collection;
  
  @Autowired
  @NonNull
  private SensorCollection _sensors;

  /**
   * Count all nodes in the application.
   * 
   * This method also allow the user to filter and group counted nodes by categories.
   * 
   * For more information about all allowed options, take a look to the NodeCollection request configuration
   * class.
   * 
   * @param request The related HTTP request.
   * 
   * @return An HTTP response with the user requested data.
   * 
   * @throws InvalidAPIRequestException When the user request is invalid / malformed.
   */
  @GetMapping("/nodes/count")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> count (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException {
    return aggregate(
      _collection, request, 
      EntityCountAggregationTransformation.create()
    );
  }

  /**
   * Fetch nodes from the application.
   * 
   * Allow the user to filter, paginate and sort returned nodes.
   * 
   * For more information about all allowed options, take a look to the NodeCollection request configuration
   * class.
   * 
   * @param request
   * @return
   * @throws InvalidAPIRequestException
   */
  @GetMapping("/nodes")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<Node>> index (@NonNull final HttpServletRequest request)
    throws InvalidAPIRequestException
  {
    return indexCollection(_collection, request);
  }
  
  @PostMapping("/nodes")
  public ResponseEntity<Void> create (
    @NonNull final HttpServletRequest request,
    @NonNull @Validated(RestGroup.EntityCreation.class) @RequestBody final NodeModifier node
  ) throws EntityNotFoundException {
    final Node added = _collection.create(node);
    
    final HttpHeaders headers = new HttpHeaders();
    headers.add("Location", request.getRequestURI() + "/" + added.getIdentifier());
    
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping("/nodes/{identifier}")
  public Node get (@PathVariable final long identifier) throws EntityNotFoundException {
    return _collection.findByIdentifierOrFail(identifier);
  }

  @GetMapping("/nodes/{identifier}/sensors")
  @ParametersFromConfiguration(
    value = SensorCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<Sensor>> getSensors (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);

    return indexCollection(_sensors.of(node), request);
  }
  
  @GetMapping("/nodes/{identifier}/sensors/count")
  @ParametersFromConfiguration(
    value = SensorCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> countSensors (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return aggregate(_sensors.of(node), request, new EntityCountAggregationTransformation<>());
  }
  
  @GetMapping("/nodes/{nodeIdentifier}/sensors/{sensorIdentifier}")
  public Sensor getSensor (
    @NonNull final HttpServletRequest request,
    @PathVariable final long nodeIdentifier,
    @PathVariable final long sensorIdentifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(nodeIdentifier);

    return _sensors.of(node).findByIdentifierOrFail(sensorIdentifier);
  }

  @GetMapping("/nodes/{identifier}/children")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<Node>> getDirectChildren (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return indexCollection(_collection.directChildrenOf(node), request);
  }
  
  @GetMapping("/nodes/{identifier}/children/count")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> countDirectChildren (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return aggregate(_collection.directChildrenOf(node), request, new EntityCountAggregationTransformation<>());
  }
  
  @GetMapping("/nodes/{nodeIdentifier}/children/{childrenIdentifer}")
  public Node getDirectChild (
    @NonNull final HttpServletRequest request,
    @PathVariable final long nodeIdentifier,
    @PathVariable final long childrenIdentifer
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(nodeIdentifier);
    return _collection.directChildrenOf(node).findByIdentifierOrFail(childrenIdentifer);
  }
  
  @GetMapping("/nodes/{identifier}/allChildren")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    groupable = false
  )
  public ResponseEntity<List<Node>> getDeepChildren (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return indexCollection(_collection.deepChildrenOf(node), request);
  }
  
  @GetMapping("/nodes/{identifier}/allChildren/count")
  @ParametersFromConfiguration(
    value = NodeCollectionRequestConfiguration.class,
    orderable = false
  )
  public ResponseEntity<Object> countDeepChildren (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(identifier);
    return aggregate(_collection.deepChildrenOf(node), request, new EntityCountAggregationTransformation<>());
  }
  
  @GetMapping("/nodes/{nodeIdentifier}/allChildren/{childrenIdentifer}")
  public Node getDeepChild (
    @NonNull final HttpServletRequest request,
    @PathVariable final long nodeIdentifier,
    @PathVariable final long childrenIdentifer
  ) throws EntityNotFoundException, InvalidAPIRequestException {
    final Node node = _collection.findByIdentifierOrFail(nodeIdentifier);
    return _collection.deepChildrenOf(node).findByIdentifierOrFail(childrenIdentifer);
  }
}
