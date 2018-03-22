package org.domus.api.controller.web;

import org.springframework.lang.NonNull;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.boot.web.servlet.error.ErrorController;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;

@Controller
public final class HttpErrorController implements ErrorController {
  @NonNull private static final String ERROR_PATH = "/error";

  @GetMapping("/error")
  public String handleHttpException (
    @NonNull final Model model,
    @NonNull final HttpServletRequest request,
    @NonNull final HttpServletResponse response
  ) {
    return "errors/http";
  }

  /**
  * @see
  */
  public String getErrorPath() {
    return HttpErrorController.ERROR_PATH;
  }
}
