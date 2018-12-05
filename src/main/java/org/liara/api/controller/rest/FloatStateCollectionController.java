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
import org.liara.api.data.entity.state.ValueState;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = {
  "states<float>"
}, produces = "application/json", consumes = "application/json", protocols = "http")
@CollectionController.Name("states<float>")
public final class FloatStateCollectionController
  extends BaseRestController<ValueState.Float>
{
  @NonNull
  private final RestCollectionControllerConfiguration _configuration;

  @Autowired
  public FloatStateCollectionController (
    @NonNull final RestCollectionControllerConfiguration configuration
  )
  {
    super(configuration);
    _configuration = configuration;
  }

  @GetMapping("/states<float>/count")
  public @NonNull Long count (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return super.count(request);
  }

  @GetMapping("/states<float>")
  public @NonNull ResponseEntity<@NonNull List<ValueState.@NonNull Float>> index (
    @NonNull final HttpServletRequest request
  )
  throws InvalidAPIRequestException
  {
    return super.index(request);
  }

  @GetMapping("/states<float>/{identifier}")
  public ValueState.@NonNull Float get (
    @NonNull @PathVariable final Long identifier
  )
  {
    return super.get(identifier);
  }

  @Override
  public @NonNull RequestConfiguration getRequestConfiguration () {
    return _configuration.getEntityConfigurationFactory().create(ValueState.Float.class);
  }

  @Override
  public @NonNull JPAEntityCollection<ValueState.Float> getCollection () {
    return new JPAEntityCollection<>(_configuration.getEntityManager(), ValueState.Float.class);
  }
}
