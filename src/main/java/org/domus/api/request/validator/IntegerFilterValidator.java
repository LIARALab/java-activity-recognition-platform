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
import org.domus.api.request.parser.IntegerFilterParser;
import org.domus.api.request.validator.error.APIRequestError;
import org.domus.api.request.validator.error.APIRequestParameterValueError;
import org.springframework.lang.NonNull;

public class IntegerFilterValidator implements FilterValidator
{

  @NonNull
  public static final Pattern PREDICATE_PATTERN   = Pattern
    .compile(IntegerFilterParser.PREDICATE_PATTERN.pattern().replaceAll("\\(\\?\\<[a-zA-Z]*?\\>", "("));

  @NonNull
  public static final Pattern CONJUNCTION_PATTERN = Pattern
    .compile(String.join("", "(", PREDICATE_PATTERN.pattern(), ")", "(,(", PREDICATE_PATTERN.pattern(), "))*"));

  @NonNull
  public static final Pattern FILTER_PATTERN      = Pattern.compile(
    String.join("", "^", "(", CONJUNCTION_PATTERN.pattern(), ")", "(;(", CONJUNCTION_PATTERN.pattern(), "))*", "$")
  );

  @NonNull
  private final String        _parameter;

  public IntegerFilterValidator(@NonNull final String parameter) {
    this._parameter = parameter;
  }

  @Override
  public List<APIRequestError> validate (APIRequest request) {
    final List<APIRequestError> errors = new ArrayList<>();

    if (request.contains(_parameter)) {
      APIRequestParameter parameter = request.getParameter(_parameter);

      for (int index = 0; index < parameter.getValueCount(); ++index) {
        this.assertIsValidParameterValue(errors, parameter, index);
      }
    }

    return errors;
  }

  private void assertIsValidParameterValue (
    @NonNull final List<APIRequestError> errors,
    @NonNull final APIRequestParameter parameter,
    final int index
  )
  {
    final String value = parameter.getValue(index);

    if (!FILTER_PATTERN.matcher(value).find()) {
      errors.add(
        APIRequestParameterValueError.create(
          parameter,
          index,
          String.join(
            "\\r\\n",
            "The given value does not match the integer filter structure :",
            "",
            "integer-filter: disjunction",
            "",
            "disjunction: conjunction(;conjunction)*",
            "",
            "conjunction: predicate(,predicate)*",
            "",
            "predicate: gt:number # Greather than",
            "         | gte:number # Greather than or equal to",
            "         | lt:number # Less than",
            "         | lte:number # Less than or equal to",
            "         | number:number # Between",
            "         | !number:number # Not between",
            "         | number # Equal",
            "         | !number # Not equal",
            "",
            "number: (\\+|\\-)?\\d+"
          )
        )
      );
    }
  }

}
