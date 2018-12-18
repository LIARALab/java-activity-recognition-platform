package org.liara.api.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.error.InvalidAPIRequestException;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface CollectionOperation
{
  interface Index<ReturnType>
  {
    @NonNull JPAEntityCollection<ReturnType> index (@NonNull final APIRequest request)
    throws InvalidAPIRequestException;
  }

  interface Count
  {
    @NonNull Long count (@NonNull final APIRequest request)
    throws InvalidAPIRequestException;
  }

  interface Get<ReturnType>
  {
    @NonNull ReturnType get (@NonNull final Long identifier)
    throws EntityNotFoundException;

    @NonNull ReturnType get (@NonNull final UUID uuid)
    throws EntityNotFoundException;
  }

  interface Create
  {
    @NonNull Long create (@NonNull final JsonNode json)
    throws JsonProcessingException, InvalidRequestBodyException;
  }
}
