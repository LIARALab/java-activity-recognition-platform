package org.domus.api.controller.web;

import org.springframework.lang.NonNull;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;

import org.springframework.ui.Model;

import java.util.List;

import org.domus.api.APIErrorMessage;
import org.domus.api.collection.exception.EntityNotFoundException;
import org.domus.api.executor.InvalidAPIRequestException;
import org.domus.api.executor.RequestError;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public final class APIExceptionHandler {
  @ExceptionHandler(InvalidAPIRequestException.class)
  public ResponseEntity<List<RequestError>> handleInvalidAPIRequestException (
    @NonNull final InvalidAPIRequestException exception
  ) {
    return new ResponseEntity<>(exception.errors(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<APIErrorMessage> handleEntityNotFoundException (
    @NonNull final EntityNotFoundException exception
  ) {
    return new ResponseEntity<>(
        new APIErrorMessage("The requested entity does not exists."), 
        HttpStatus.NOT_FOUND
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Exception> handleException (
    @NonNull final Exception exception
  ) {
    return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
