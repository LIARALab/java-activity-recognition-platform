/*******************************************************************************
 * Copyright (C) 2018 C�dric DEMONGIVERT <cedric.demongivert@gmail.com>
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

import org.liara.api.collection.exception.EntityNotFoundException;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.request.validator.error.InvalidAPIRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.lang.NonNull;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

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
public final class NodeCollectionController extends BaseRestController
{
  @Autowired
  @NonNull
  private NodeCollection _collection;

  @GetMapping("/nodes/count")
  public ResponseEntity<Object> count (@NonNull final HttpServletRequest request) throws InvalidAPIRequestException {
    return aggregate(_collection, request, this::count);
  }

  @GetMapping("/nodes")
  @ApiImplicitParams(
    {
      @ApiImplicitParam(
          name = "first",
          value = "Maximum number of elements to display. Must be a positive integer and can't be used in conjunction with \"all\".",
          required = false,
          allowMultiple = false,
          defaultValue = "10",
          dataType = "unsigned int",
          paramType = "query"
      ),
      @ApiImplicitParam(
          name = "all",
          value = "Display all remaining elements. Can't be used in conjunction with \"first\".",
          required = false,
          allowMultiple = false,
          defaultValue = "false",
          dataType = "boolean",
          paramType = "query"
      ),
      @ApiImplicitParam(
          name = "after",
          value = "Number of elements to skip.",
          required = false,
          allowMultiple = false,
          defaultValue = "0",
          dataType = "unsigned int",
          paramType = "query"
      )
    }
  )
  public ResponseEntity<List<Node>> index (@NonNull final HttpServletRequest request)
    throws InvalidAPIRequestException
  {
    return indexCollection(_collection, request);
  }

  @GetMapping("/nodes/{identifier}")
  public Node get (@PathVariable final long identifier) throws EntityNotFoundException {
    return _collection.findByIdOrFail(identifier);
  }

  @GetMapping("/nodes/{identifier}/sensors")
  public List<Sensor> getSensors (@PathVariable final long identifier) throws EntityNotFoundException {
    return _collection.findByIdOrFail(identifier).getSensors();
  }

  @GetMapping("/nodes/{identifier}/children")
  public List<Node> getChildren (@PathVariable final long identifier) throws EntityNotFoundException {
    return _collection.getAllChildren(identifier);
  }
}
