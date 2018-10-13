/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
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
package org.liara.api.request.cursor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.request.APIRequest;
import org.liara.request.APIRequestParameter;
import org.liara.request.validator.APIRequestValidation;
import org.liara.request.validator.APIRequestValidator;
import org.liara.request.validator.error.APIRequestError;
import org.liara.request.validator.error.APIRequestParameterError;
import org.liara.request.validator.error.APIRequestParameterValueError;

import java.util.regex.Pattern;

/**
 * Validate a request for a future parse with an APIRequestFreeCursorParser
 * 
 * @author C&eacute;dric DEMONGIVERT [cedric.demongivert@gmail.com](mailto:cedric.demongivert@gmail.com)
 */
public class APIRequestFreeCursorValidator implements APIRequestValidator
{
  private final static Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("^(\\+)?\\d+$");
  private final static Pattern BOOLEAN_PATTERN          = Pattern.compile("^(true|false|1|0|)?$");

  @Override
  public @NonNull APIRequestValidation validate (@NonNull final APIRequest request) {
    return APIRequestValidation.concat(isValidRequest(request),
                                       hasValidAllParameter(request),
                                       hasValidOffsetParameter(request),
                                       hasValidLimitParameter(request)
    );
  }

  /**
   * Check if the "first" parameter of the given request is valid.
   *
   * @param request Request to check.
   */
  private @NonNull APIRequestValidation hasValidLimitParameter (@NonNull final APIRequest request) {
    @NonNull APIRequestValidation result = new APIRequestValidation(request);

    if (request.contains("first")) {
      @NonNull final APIRequestParameter parameter = request.getParameter("first");

      if (parameter.getSize() > 1) {
        result = result.addError(createMoreThanOneParameterError(parameter));
      }

      for (int index = 0; index < parameter.getSize(); ++index) {
        final String value = parameter.get(index).get().trim();

        if (!POSITIVE_INTEGER_PATTERN.matcher(value).find()) {
          result = result.addError(createNotAPositiveIntegerError(parameter, index));
        }
      }
    }

    return result;
  }

  /**
   * Check if the "after" parameter of the given request is valid.
   *
   * @param request Request to check.
   */
  private @NonNull APIRequestValidation hasValidOffsetParameter (@NonNull final APIRequest request) {
    @NonNull APIRequestValidation result = new APIRequestValidation(request);

    if (request.contains("after")) {
      @NonNull final APIRequestParameter parameter = request.getParameter("after");

      if (parameter.getSize() > 1) {
        result = result.addError(createMoreThanOneParameterError(parameter));
      }

      for (int index = 0; index < parameter.getSize(); ++index) {
        final String value = parameter.get(index).get().trim();

        if (!POSITIVE_INTEGER_PATTERN.matcher(value).find()) {
          result = result.addError(createNotAPositiveIntegerError(parameter, index));
        }
      }
    }

    return result;
  }

  /**
   * Check if the "all" parameter of the given request is valid.
   *
   * @param request Request to check.
   */
  private @NonNull APIRequestValidation hasValidAllParameter (@NonNull final APIRequest request) {
    @NonNull APIRequestValidation result = new APIRequestValidation(request);

    if (request.contains("all")) {
      final APIRequestParameter parameter = request.getParameter("all");

      if (parameter.getSize() > 1) {
        result = result.addError(createMoreThanOneParameterError(parameter));
      }

      for (int index = 0; index < parameter.getSize(); ++index) {
        final String value = parameter.get(index).get().trim();

        if (!BOOLEAN_PATTERN.matcher(value).find()) {
          result = result.addError(createNotABooleanError(parameter, index));
        }
      }
    }

    return result;
  }

  /**
   * Check if not both of "all" and "first" parameters are present at the same
   * time in a request.
   *
   * @param request Request to check.
   */
  private @NonNull APIRequestValidation isValidRequest (@NonNull final APIRequest request) {
    if (request.contains("all") && request.contains("first") && request.getParameter("all").getAsBoolean(0).get()) {
      return new APIRequestValidation(request).addError(createAllAndFirstError(request));
    } else {
      return new APIRequestValidation(request);
    }
  }

  private @NonNull APIRequestError createAllAndFirstError (@NonNull final APIRequest request) {
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

  private @NonNull APIRequestError createMoreThanOneParameterError (@NonNull final APIRequestParameter parameter) {
    return new APIRequestParameterError(
      parameter,
      String.join(
        "",
        "Only one parameter is allowed by request, the given parameter was set ",
        String.valueOf(parameter.getSize()),
        " times."
      )
    );
  }

  private @NonNull APIRequestError createNotAPositiveIntegerError (
    @NonNull final APIRequestParameter parameter, final int index
  )
  {
    return APIRequestParameterValueError.create(parameter,
                                                index,
                                                "The given parameter value must be a positive integer."
    );
  }

  private @NonNull APIRequestError createNotABooleanError (
    @NonNull final APIRequestParameter parameter, final int index
  )
  {
    return APIRequestParameterValueError.create(parameter,
                                                index,
                                                "The given parameter value must be a boolean (true, false, or nothing)."
    );
  }
}
