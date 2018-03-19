package org.domus.api.controller;

import org.springframework.lang.NonNull;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.http.HttpStatus;

import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public final class APIExceptionHandler {
  @ExceptionHandler(Exception.class)
  public String handleException (
    @NonNull final Exception exception,
    @NonNull final Model model
  ) {
    model.addAttribute("exception", exception);
    return "errors/exception";
  }
}
