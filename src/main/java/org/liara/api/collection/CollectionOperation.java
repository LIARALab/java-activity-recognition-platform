package org.liara.api.collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.validation.InvalidModelException;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.request.APIRequest;
import org.liara.request.validator.error.InvalidAPIRequestException;
import reactor.core.publisher.Mono;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface CollectionOperation
{
  interface Index<ReturnType>
  {
    @NonNull JPAEntityCollection<ReturnType> index (@NonNull final APIRequest request)
    throws InvalidAPIRequestException;

    default @NonNull Mono<@NonNull JPAEntityCollection<ReturnType>> indexOrFail (@NonNull final APIRequest request) {
      try {
        return Mono.just(index(request));
      } catch (@NonNull final InvalidAPIRequestException exception) {
        return Mono.error(exception);
      }
    }
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

    default @NonNull Mono<@NonNull ReturnType> getOrFail (@NonNull final Long identifier) {
      try {
        return Mono.just(get(identifier));
      } catch (@NonNull final EntityNotFoundException exception) {
        return Mono.error(exception);
      }
    }

    @NonNull ReturnType get (@NonNull final UUID uuid)
    throws EntityNotFoundException;

    default @NonNull Mono<@NonNull ReturnType> getOrFail (@NonNull final UUID uuid) {
      try {
        return Mono.just(get(uuid));
      } catch (@NonNull final EntityNotFoundException exception) {
        return Mono.error(exception);
      }
    }
  }

  interface Create
  {
    @NonNull Long create (@NonNull final JsonNode json)
    throws JsonProcessingException, InvalidModelException;

    default @NonNull Mono<@NonNull Long> createOrFail (@NonNull final JsonNode json) {
      try {
        return Mono.just(create(json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    }
  }

  interface Set
  {
    @NonNull Long set (@NonNull final Long identifier, @NonNull final JsonNode json)
    throws JsonProcessingException, InvalidModelException;

    default @NonNull Mono<@NonNull Long> setOrFail (@NonNull final Long identifier, @NonNull final JsonNode json) {
      try {
        return Mono.just(set(identifier, json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    }

    @NonNull Long set (@NonNull final UUID identifier, @NonNull final JsonNode json)
    throws JsonProcessingException, InvalidModelException;

    default @NonNull Mono<@NonNull Long> setOrFail (@NonNull final UUID identifier, @NonNull final JsonNode json) {
      try {
        return Mono.just(set(identifier, json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    }
  }

  interface Mutate
  {
    @NonNull Long mutate (@NonNull final Long identifier, @NonNull final JsonNode json)
    throws EntityNotFoundException, JsonProcessingException, InvalidModelException;

    default @NonNull Mono<@NonNull Long> mutateOrFail (@NonNull final Long identifier, @NonNull final JsonNode json) {
      try {
        return Mono.just(mutate(identifier, json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    }

    @NonNull Long mutate (@NonNull final UUID identifier, @NonNull final JsonNode json)
    throws EntityNotFoundException, JsonProcessingException, InvalidModelException;

    default @NonNull Mono<@NonNull Long> mutateOrFail (@NonNull final UUID identifier, @NonNull final JsonNode json) {
      try {
        return Mono.just(mutate(identifier, json));
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    }
  }

  interface Delete
  {
    void delete (@NonNull final Long identifier)
    throws EntityNotFoundException;

    default @NonNull Mono<Void> deleteOrFail (@NonNull final Long identifier) {
      try {
        delete(identifier);
        return Mono.empty();
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    }

    void delete (@NonNull final UUID identifier)
    throws EntityNotFoundException;

    default @NonNull Mono<Void> deleteOrFail (@NonNull final UUID identifier) {
      try {
        delete(identifier);
        return Mono.empty();
      } catch (@NonNull final Exception exception) {
        return Mono.error(exception);
      }
    }
  }
}
