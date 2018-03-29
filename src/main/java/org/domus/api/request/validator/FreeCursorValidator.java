/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.domus.api.request.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.domus.api.request.APIRequest;
import org.domus.api.request.APIRequestParameter;
import org.domus.api.request.validator.error.APIRequestError;
import org.domus.api.request.validator.error.APIRequestParameterError;
import org.domus.api.request.validator.error.APIRequestParameterValueError;
import org.springframework.lang.NonNull;

/**
 * Validate a request for a future parse with
 * org.domus.api.request.parser.FreeCursorParser
 * 
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class FreeCursorValidator implements APIRequestValidator
{
  private final static Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^(\\+)?\\d+$");
  private final static Pattern BOOLEAN_PATTERN          = Pattern.compile("^(true|false)?$");

  /**
   * @see org.domus.api.request.validator.APIRequestValidator#validate(org.domus.api.request.APIRequest)
   */
  @Override
  public List<APIRequestError> validate (@NonNull final APIRequest request) {
    final List<APIRequestError> errors = new ArrayList<>();

    this.assertIsValidRequest(errors, request);
    this.assertHasValidAllParameter(errors, request);
    this.assertHasValidOffsetParameter(errors, request);
    this.assertHasValidLimitParameter(errors, request);

    return errors;
  }

  /**
   * Check if the "first" parameter of the given request is valid.
   *
   * @param errors A collection of errors to feed.
   * @param request Request to check.
   */
  private
    void
    assertHasValidLimitParameter (@NonNull final List<APIRequestError> errors, @NonNull final APIRequest request)
  {
    if (request.contains("first")) {
      final APIRequestParameter parameter = request.getParameter("first");

      if (parameter.getValueCount() > 1) {
        errors.add(this.createMoreThanOneParameterError(parameter));
      }

      for (int index = 0; index < parameter.getValueCount(); ++index) {
        final String value = parameter.getValue(index).trim();

        if (!POSITIVE_INTEGER_PATTERN.matcher(value).find()) {
          errors.add(this.createNotAPositiveIntegerError(parameter, index));
        }
      }
    }
  }

  /**
   * Check if the "after" parameter of the given request is valid.
   *
   * @param errors A collection of errors to feed.
   * @param request Request to check.
   */
  private
    void
    assertHasValidOffsetParameter (@NonNull final List<APIRequestError> errors, @NonNull final APIRequest request)
  {
    if (request.contains("after")) {
      final APIRequestParameter parameter = request.getParameter("after");

      if (parameter.getValueCount() > 1) {
        errors.add(this.createMoreThanOneParameterError(parameter));
      }

      for (int index = 0; index < parameter.getValueCount(); ++index) {
        final String value = parameter.getValue(index).trim();

        if (!POSITIVE_INTEGER_PATTERN.matcher(value).find()) {
          errors.add(this.createNotAPositiveIntegerError(parameter, index));
        }
      }
    }
  }

  /**
   * Check if the "all" parameter of the given request is valid.
   *
   * @param errors A collection of error to feed.
   * @param request Request to check.
   */
  private
    void
    assertHasValidAllParameter (@NonNull final List<APIRequestError> errors, @NonNull final APIRequest request)
  {
    if (request.contains("all")) {
      final APIRequestParameter parameter = request.getParameter("all");

      if (parameter.getValueCount() > 1) {
        errors.add(this.createMoreThanOneParameterError(parameter));
      }

      for (int index = 0; index < parameter.getValueCount(); ++index) {
        final String value = parameter.getValue(index).trim();

        if (!BOOLEAN_PATTERN.matcher(value).find()) {
          errors.add(this.createNotABooleanError(parameter, index));
        }
      }
    }
  }

  /**
   * Check if not both of "all" and "first" parameters are present at the same
   * time in a request.
   *
   * @param errors A collection of error to feed.
   * @param request Request to check.
   */
  private void assertIsValidRequest (@NonNull final List<APIRequestError> errors, @NonNull final APIRequest request) {
    if (request.contains("all") && request.contains("first") && !request.getValue("all", 0).trim().equals("false")) {
      errors.add(this.createAllAndFirstError(request));
    }
  }

  private APIRequestError createAllAndFirstError (@NonNull final APIRequest request) {
    return new APIRequestError(
      request,
      String.join(
        "",
        "\"all\" and \"first\" parameters can't be both present on the same request. You have ",
        "to choose one of them in accordance with the result that you ",
        "expect to get. Refer to the documentation for more information."
      )
    );
  }

  private APIRequestError createMoreThanOneParameterError (@NonNull final APIRequestParameter parameter) {
    return APIRequestParameterError.create(
      parameter,
      String.join(
        "",
        "Only one parameter is allowed by request, the given parameter was setted ",
        String.valueOf(parameter.getValueCount()),
        " times."
      )
    );
  }

  private
    APIRequestError
    createNotAPositiveIntegerError (@NonNull final APIRequestParameter parameter, final int index)
  {
    return APIRequestParameterValueError
      .create(parameter, index, "The given parameter value must be a positive integer.");
  }

  private APIRequestError createNotABooleanError (@NonNull final APIRequestParameter parameter, final int index) {
    return APIRequestParameterValueError
      .create(parameter, index, "The given parameter value must be a boolean (true, false, or nothing).");
  }
}
