package org.liara.api.controller.rest;

import io.swagger.annotations.Api;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.CollectionFactory;
import org.liara.api.data.entity.state.ActivityState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(
    tags = {
      "state<activity>"
    },
    description = "All activities-related operation.",
    produces = "application/json",
    consumes = "application/json",
    protocols = "http"
)
public class ActivitiesCollectionController
  extends BaseRestController
{
  @Autowired
  public ActivitiesCollectionController (
    @NonNull final CollectionFactory collections
  )
  {
    super(collections);
  }

  @GetMapping("/states<activity>/count")
  public @NonNull Long count (
    @NonNull final HttpServletRequest request
  )
  {
    return count(ActivityState.class, request);
  }

  @GetMapping("/states<activity>")
  public @NonNull ResponseEntity<@NonNull List<@NonNull ActivityState>> index (
    @NonNull final HttpServletRequest request
  )
  {
    return index(ActivityState.class, request);
  }

  @GetMapping("/states<activity>/{identifier}")
  public @NonNull ActivityState get (
    @PathVariable final Long identifier
  )
  {
    return get(ActivityState.class, identifier);
  }
}
