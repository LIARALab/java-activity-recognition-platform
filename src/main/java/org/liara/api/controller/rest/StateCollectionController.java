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

import io.swagger.annotations.Api;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.CollectionController;
import org.liara.api.collection.configuration.RequestConfiguration;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.event.ApplicationEntityEvent;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

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
@CollectionController.Name("states")
public class StateCollectionController
  extends BaseRestController<State>
{
  @NonNull
  private final RestCollectionControllerConfiguration _configuration;

  @Autowired
  public StateCollectionController (
    @NonNull final RestCollectionControllerConfiguration configuration
  )
  {
    super(configuration);
    _configuration = configuration;
  }

  @GetMapping("/states/count")
  public @NonNull Long count (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return super.count(request);
  }

  @GetMapping("/states")
  public @NonNull ResponseEntity<@NonNull List<@NonNull State>> index (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return super.index(request);
  }

  @GetMapping("/states/{identifier}")
  public @NonNull State get (
    @NonNull @PathVariable final Long identifier
  )
  {
    return super.get(identifier);
  }

  @GetMapping("/states/{identifier}/sensor")
  public @NonNull Sensor getSensor (
    @PathVariable final Long identifier
  )
  {
    return _configuration.getEntityManager().find(Sensor.class, get(identifier).getSensorIdentifier());
  }

  @PostMapping("/states")
  @Transactional
  public @NonNull ResponseEntity<Void> create (
    @NonNull final HttpServletRequest request, @NonNull @Valid @RequestBody final State state
  ) {
    _configuration.getApplicationEventPublisher().publishEvent(new ApplicationEntityEvent.Create(this, state));
    
    final HttpHeaders headers = new HttpHeaders();
    headers.add("Location", request.getRequestURI() + "/" + state.getIdentifier());
    
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }
  
  @PatchMapping("/states/{identifier}")
  @Transactional
  public ResponseEntity<?> update (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier, @NonNull @RequestBody final State update
  )
  {
    update.setIdentifier(identifier);

    _configuration.getApplicationEventPublisher().publishEvent(new ApplicationEntityEvent.Update(this, update));
    
    return new ResponseEntity<Void>(HttpStatus.OK);
  }


  @Override
  public @NonNull RequestConfiguration getRequestConfiguration () {
    return _configuration.getEntityConfigurationFactory().create(State.class);
  }

  @Override
  public @NonNull JPAEntityCollection<State> getCollection () {
    return new JPAEntityCollection<>(_configuration.getEntityManager(), State.class);
  }
}
