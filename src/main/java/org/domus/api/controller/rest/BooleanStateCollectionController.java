package org.domus.api.controller.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.lang.NonNull;

import org.domus.api.data.repository.BooleanStateRepository;
import org.domus.api.executor.InvalidAPIRequestException;
import org.domus.api.collection.EntityCollections;
import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.data.entity.BooleanState;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

@RestController
@Api(
    tags = {
      "state<boolean>"
    },
    description = "All sensor-related operation.",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public final class BooleanStateCollectionController extends BaseRestController
{
  @Autowired
  @NonNull
  private EntityCollections _collections;

  @GetMapping("/states<boolean>/count")
  public int count (@NonNull final HttpServletRequest request) {
    return _collections.create(BooleanState.class).getSize();
  }

  @GetMapping("/states<boolean>")
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
  public ResponseEntity<Iterable<BooleanState>> index (@NonNull final HttpServletRequest request)
    throws InvalidAPIRequestException
  {
    return this.indexCollection(BooleanState.class, request);
  }

  @GetMapping("/states<boolean>/{identifier}")
  public BooleanState get (@PathVariable final int identifier) throws EntityNotFoundException {
    return _collections.create(BooleanState.class).findByIdOrFail(identifier);
  }
}
