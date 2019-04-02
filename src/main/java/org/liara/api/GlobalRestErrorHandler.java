package org.liara.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.misc.OrderedHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.request.validator.error.InvalidAPIRequestException;
import org.liara.rest.error.IllegalRestRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class GlobalRestErrorHandler
  extends AbstractErrorWebExceptionHandler
{
  @NonNull
  private final ObjectMapper _objectMapper;

  @Autowired
  public GlobalRestErrorHandler (
    @NonNull final ErrorAttributes errorAttributes,
    @NonNull final ResourceProperties resourceProperties,
    @NonNull final ApplicationContext applicationContext,
    @NonNull final ServerCodecConfigurer serverCodecConfigurer,
    @NonNull final ObjectMapper objectMapper
  ) {
    super(errorAttributes, resourceProperties, applicationContext);
    setMessageReaders(serverCodecConfigurer.getReaders());
    setMessageWriters(serverCodecConfigurer.getWriters());
    _objectMapper = objectMapper;
  }

  @Override
  protected @NonNull RouterFunction<ServerResponse> getRoutingFunction (
    @NonNull final ErrorAttributes errorAttributes
  ) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private @NonNull Mono<ServerResponse> renderErrorResponse (@NonNull final ServerRequest request) {
    @NonNull final Throwable throwable = getError(request);

    if (throwable instanceof NoSuchElementException) {
      return render404Error(request);
    }

    if (throwable instanceof MethodNotAllowedException) {
      return render405Error((MethodNotAllowedException) throwable, request);
    }

    if (throwable instanceof ResponseStatusException) {
      switch (((ResponseStatusException) throwable).getStatus()) {
        case NOT_FOUND: return render404Error(request);
      }
    }

    if (throwable instanceof IllegalRestRequestException) {
      return renderIllegalRestRequestException((IllegalRestRequestException) throwable, request);
    }

    return render500Error(request);
  }

  private @NonNull Mono<ServerResponse> renderIllegalRestRequestException (
    @NonNull final IllegalRestRequestException exception,
    @NonNull final ServerRequest request
  ) {
    if (exception.getCause() instanceof InvalidAPIRequestException) {
      return renderInvalidAPIRequestException((InvalidAPIRequestException) exception.getCause());
    }

    return render500Error(request);
  }

  private @NonNull Mono<ServerResponse> renderInvalidAPIRequestException (
    @NonNull final InvalidAPIRequestException exception
  ) {
    @NonNull final Map<@NonNull String, @NonNull Object> body = new HashMap<>();
    body.put("errors", exception.getErrors());
    body.put("request", exception.getRequest());

    return ServerResponse.status(HttpStatus.BAD_REQUEST)
             .contentType(MediaType.APPLICATION_JSON)
             .syncBody(body);
  }

  private @NonNull Mono<ServerResponse> render404Error (@NonNull final ServerRequest request) {
    return ServerResponse.status(HttpStatus.NOT_FOUND).build();
  }

  private @NonNull Mono<ServerResponse> render405Error (
    @NonNull final MethodNotAllowedException throwable,
    @NonNull final ServerRequest request
  ) {
    return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED)
             .header(
               HttpHeaders.ALLOW,
               throwable.getSupportedMethods()
                 .stream()
                 .map(HttpMethod::name)
                 .collect(Collectors.joining(","))
             ).build();
  }

  private @NonNull Mono<ServerResponse> render500Error (@NonNull final ServerRequest request) {
    getError(request).printStackTrace();

    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
             .contentType(MediaType.APPLICATION_JSON)
             .syncBody(
               _objectMapper.valueToTree(makeMapFromError(getError(request)))
             );
  }

  private @NonNull Map<@NonNull String, @NonNull Object> makeMapFromError (
    @NonNull final Throwable error
  ) {
    @NonNull final Map<@NonNull String, @NonNull Object> result = new OrderedHashMap<>();

    result.put("message", error.getLocalizedMessage());
    result.put("type", error.getClass().getName());

    @NonNull final List<@NonNull String> stack = new LinkedList<>();
    Arrays.stream(error.getStackTrace()).map(StackTraceElement::toString).forEach(stack::add);
    result.put("stack", stack);

    if (error.getCause() != null) result.put("cause", makeMapFromError(error.getCause()));

    return result;
  }
}
