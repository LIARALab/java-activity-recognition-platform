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

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.domus.api.date.PartialLocalDateTime;
import org.domus.api.request.APIRequest;
import org.domus.api.request.APIRequestParameter;
import org.domus.api.request.parser.DateTimeFilterParser;
import org.domus.api.request.validator.error.APIRequestError;
import org.domus.api.request.validator.error.APIRequestParameterValueError;
import org.springframework.lang.NonNull;

public class DateTimeFilterValidator implements FilterValidator
{
  @NonNull
  public static final Pattern  PREDICATE_PATTERN   = Pattern
    .compile(DateTimeFilterParser.PREDICATE_PATTERN.pattern().replaceAll("\\(\\?\\<[a-zA-Z]*?\\>", "("));

  @NonNull
  private static final Pattern CONJUNCTION_PATTERN = Pattern
    .compile(String.join("", "(", PREDICATE_PATTERN.pattern(), ")", "(,(", PREDICATE_PATTERN.pattern(), "))*"));

  @NonNull
  private static final Pattern FILTER_PATTERN      = Pattern.compile(
    String.join("", "^", "(", CONJUNCTION_PATTERN.pattern(), ")", "(;(", CONJUNCTION_PATTERN.pattern(), "))*", "$")
  );

  @NonNull
  private final String         _parameter;

  public DateTimeFilterValidator(@NonNull final String parameter) {
    this._parameter = parameter;
  }

  @Override
  public List<APIRequestError> validate (@NonNull final APIRequest request) {
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
      errors.add(createWrongFilterFormatError(parameter, index));
    } else {
      this.assertContainsValidDates(errors, parameter, index);
    }
  }
  

  private APIRequestError createWrongFilterFormatError (
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index
  ) {
    return APIRequestParameterValueError.create(
      parameter,
      index,
      String.join(
        "\\r\\n",
        "The given value does not match the date filter structure :",
        "",
        "date-filter: disjunction",
        "",
        "disjunction: conjunction(;conjunction)*",
        "",
        "conjunction: predicate(,predicate)*",
        "",
        "predicate: gt:date # Greather than",
        "         | gte:date # Greather than or equal to",
        "         | lt:date # Less than",
        "         | lte:date # Less than or equal to",
        "         | dateRange # Between",
        "         | !dateRange # Not between",
        "         | date # Equal",
        "         | !date # Not equal",
        "",
        "date: \\[date-std\\]",
        "    | \\[date-format\\]\\[date-value\\]",
        "",
        "dateRange: (\\[date-format\\])?\\[date-value\\]:\\[date-value\\]",
        "",
        "date-std: @see java.time.format.DateTimeFormatter uuuu-MM-dd'T'HH:mm:ss.SSS",
        "",
        "date-format: @see java.time.format.DateTimeFormatter",
        "",
        "date-value: (.*?)"
      )
    );
  }

  private void assertContainsValidDates (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index
  ) {
    Arrays.stream(parameter.getValue(index).split(";"))
          .forEach(token -> validateConjunction(errors, parameter, index, token.trim()));
  }

  private void validateConjunction (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String value
  )
  {
    Arrays.stream(value.split(","))
          .forEach(token -> validatePredicate(errors, parameter, index, token.trim()));
  }


  private void validatePredicate (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String value
  )
  {
    final Matcher matcher = DateTimeFilterParser.PREDICATE_PATTERN.matcher(value);
    matcher.matches();
    matcher.groupCount();

    String result = null;

    if ((result = matcher.group("greaterThan")) != null) {
      this.validateGreaterThan(errors, parameter, index, result);
    } else if ((result = matcher.group("greaterThanOrEqualTo")) != null) {
      this.validateGreaterThanOrEqualTo(errors, parameter, index, result);
    } else if ((result = matcher.group("lessThan")) != null) {
      this.validateLessThan(errors, parameter, index, result);
    } else if ((result = matcher.group("lessThanOrEqualTo")) != null) {
      this.validateLessThanOrEqualTo(errors, parameter, index, result);
    } else if ((result = matcher.group("between")) != null) {
      this.validateBetween(errors, parameter, index, result);
    } else if ((result = matcher.group("notBetween")) != null) {
      this.validateNotBetweenThan(errors, parameter, index, result);
    } else if ((result = matcher.group("equalTo")) != null) {
      this.validateEqualTo(errors, parameter, index, result);
    } else {
      this.validateNotEqualTo(errors, parameter, index, matcher.group("notEqualTo"));
    }
  }

  private void validateNotEqualTo (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String result
  ) {
    validateEqualTo(errors, parameter, index, result.substring(1));
  }

  private void validateEqualTo (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String result
  ) {
    this.validateDate(errors, parameter, index, result);
  }

  private void validateNotBetweenThan (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String result
  ) {
    this.validateBetween(errors, parameter, index, result.substring(1));
  }

  private void validateBetween (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String result
  ) {
    final Matcher matcher = DateTimeFilterParser.BETWEEN_PATTERN.matcher(result);
    matcher.matches();
    matcher.groupCount();
    
    final String dateFormat = matcher.group("dateFormat");
    final String firstDateValue = matcher.group("firstDateValue");
    final String secondDateValue = matcher.group("secondDateValue");

    final String pattern = (dateFormat == null) ? DateTimeFilterParser.DEFAULT_DATE_FORMAT_PATTERN : dateFormat;
    
    this.validateParsedDate(errors, parameter, index, pattern, firstDateValue);
    this.validateParsedDate(errors, parameter, index, pattern, secondDateValue);
  }

  private void validateLessThanOrEqualTo (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String result
  ) {
    validateDate(errors, parameter, index, result.split("lte:")[1]);
  }

  private void validateLessThan (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String result
  ) {
    validateDate(errors, parameter, index, result.split("lt:")[1]);
  }

  private void validateGreaterThanOrEqualTo (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String result
  ) {
    validateDate(errors, parameter, index, result.split("gte:")[1]);
  }

  private void validateGreaterThan (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String result
  ) {
    validateDate(errors, parameter, index, result.split("gt:")[1]);
  }
  
  private void validateDate (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String date
  ) {
    final Matcher matcher = DateTimeFilterParser.DATE_PATTERN.matcher(date);
    matcher.matches();
    matcher.groupCount();

    final String standardDate = matcher.group("standardDate");
    final String dateFormat = matcher.group("dateFormat");
    final String dateValue = matcher.group("dateValue");
    
    if (standardDate != null) {
      validateParsedDate(errors, parameter, index, DateTimeFilterParser.DEFAULT_DATE_FORMAT_PATTERN, standardDate);
    } else {
      validateParsedDate(errors, parameter, index, dateFormat, dateValue);
    }
  }

  private void validateParsedDate (
    @NonNull final List<APIRequestError> errors, 
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index,
    @NonNull final String pattern,
    @NonNull final String date
  ) {
   DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
    
    try {
      format.parse(date);
    } catch (final DateTimeParseException e) {
      errors.add(
        createInvalidDateValueError(
          parameter, 
          index, 
          date,
          pattern
        )
      );
    } catch (final IllegalArgumentException e) {
      errors.add(
        createInvalidDateFormatError(
          parameter, 
          index, 
          pattern
        )
      );
    }
  }

  private APIRequestError createInvalidDateFormatError (
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String dateFormat
  ) {
    return APIRequestParameterValueError.create(
      parameter, 
      index,
      String.join(
        "",
        "The custom date format \"", dateFormat, "\" is not a valid DateTime format",
        " see java.time.format.DateTimeFormatter documentation for more information about",
        " this error."
      )
    );
  }

  private APIRequestError createInvalidDateValueError (
    @NonNull final APIRequestParameter parameter, 
    @NonNull final int index, 
    @NonNull final String dateValue,
    @NonNull final String dateFormat
  ) {
    return APIRequestParameterValueError.create(
      parameter, 
      index,
      String.join(
        "",
        "The given date value \"", dateValue, "\" does not match the given format",
        " \"", dateFormat, "\"."
      )
    );
  }
}
