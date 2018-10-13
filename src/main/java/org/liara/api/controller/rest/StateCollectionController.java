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

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.CollectionFactory;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.data.schema.StateCreationSchema;
import org.liara.api.data.schema.StateMutationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
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
public class StateCollectionController
  extends BaseRestController
{
  @NonNull
  private final ObjectMapper _mapper;

  @NonNull
  private final ApplicationContext _context;

  @NonNull
  private final Validator _validator;

  @NonNull
  private final SchemaManager _schemaManager;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public StateCollectionController (
    @NonNull final ObjectMapper mapper,
    @NonNull final ApplicationContext context,
    @NonNull final Validator validator,
    @NonNull final SchemaManager schemaManager,
    @NonNull final EntityManager entityManager,
    @NonNull final CollectionFactory collections
  )
  {
    super(collections);
    _mapper = mapper;
    _context = context;
    _validator = validator;
    _schemaManager = schemaManager;
    _entityManager = entityManager;
  }

  @GetMapping("/states/count")
  public @NonNull Long count (
    @NonNull final HttpServletRequest request
  )
  {
    return count(State.class, request);
  }

  @GetMapping("/states")
  public @NonNull ResponseEntity<@NonNull List<@NonNull State>> index (
    @NonNull final HttpServletRequest request
  )
  {
    return index(State.class, request);
  }

  @GetMapping("/states/{identifier}")
  public @NonNull State get (
    @PathVariable final Long identifier
  )
  {
    return get(State.class, identifier);
  }

  @GetMapping("/states/{identifier}/sensor")
  public @NonNull Sensor getSensor (
    @PathVariable final Long identifier
  )
  {
    return get(State.class, identifier).getSensor();
  }

  @PostMapping("/states")
  @Transactional
  public @NonNull ResponseEntity<Void> create (
    @NonNull final HttpServletRequest request,
    @NonNull @Valid @RequestBody final StateCreationSchema schema
  ) {
    final State state = _schemaManager.execute(schema);
    
    final HttpHeaders headers = new HttpHeaders();
    headers.add("Location", request.getRequestURI() + "/" + state.getIdentifier());
    
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }
  
  @PatchMapping("/states/{identifier}")
  @Transactional
  public ResponseEntity<?> update (
    @NonNull final HttpServletRequest request,
    @PathVariable final long identifier,
    @NonNull @RequestBody final String jsonText,
    @NonNull final Errors errors
  ) throws IOException {
    final TreeNode json = _mapper.readTree(jsonText);
    
    if (json.isObject()) {
      final ObjectNode node = (ObjectNode) json;
      node.set("identifier", _mapper.valueToTree(identifier));
    }
    
    final StateMutationSchema schema = _mapper.treeToValue(json, StateMutationSchema.class);
    
    ValidationUtils.invokeValidator(_validator, schema, errors);
    
    if (errors.hasErrors()) {
      return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    _schemaManager.execute(schema);
    
    return new ResponseEntity<Void>(HttpStatus.OK);
  }


}
