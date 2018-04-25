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
package org.liara.api.filter.validator;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.liara.api.filter.parser.DateTimeFilterParser;
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

  public List<String> validate (@NonNull final String value)
  {
    if (!FILTER_PATTERN.matcher(value).find()) {
      return createWrongFilterFormatError();
    } else {
      return assertContainsValidDates(value);
    }
  }
  
  private List<String> createWrongFilterFormatError () {
    return Arrays.asList(
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

  private List<String> assertContainsValidDates (@NonNull final String value) {
    final List<String> result = new ArrayList<>();
    
    Arrays.stream(value.split(";"))
          .forEach(token -> validateConjunction(result, token.trim()));
    
    return result;
  }

  private void validateConjunction (@NonNull final List<String> errors, @NonNull final String value)
  {
    Arrays.stream(value.split(","))
          .forEach(token -> validatePredicate(errors, token.trim()));
  }


  private void validatePredicate (@NonNull final List<String> errors, @NonNull final String value)
  {
    final Matcher matcher = DateTimeFilterParser.PREDICATE_PATTERN.matcher(value);
    matcher.matches();
    matcher.groupCount();

    String result = null;

    if ((result = matcher.group("greaterThan")) != null) {
      this.validateGreaterThan(errors, result);
    } else if ((result = matcher.group("greaterThanOrEqualTo")) != null) {
      this.validateGreaterThanOrEqualTo(errors, result);
    } else if ((result = matcher.group("lessThan")) != null) {
      this.validateLessThan(errors, result);
    } else if ((result = matcher.group("lessThanOrEqualTo")) != null) {
      this.validateLessThanOrEqualTo(errors, result);
    } else if ((result = matcher.group("between")) != null) {
      this.validateBetween(errors, result);
    } else if ((result = matcher.group("notBetween")) != null) {
      this.validateNotBetweenThan(errors, result);
    } else if ((result = matcher.group("equalTo")) != null) {
      this.validateEqualTo(errors, result);
    } else {
      this.validateNotEqualTo(errors, matcher.group("notEqualTo"));
    }
  }

  private void validateNotEqualTo (
    @NonNull final List<String> errors, 
    @NonNull final String result
  ) {
    validateEqualTo(errors, result.substring(1));
  }

  private void validateEqualTo (
    @NonNull final List<String> errors,
    @NonNull final String result
  ) {
    this.validateDate(errors, result);
  }

  private void validateNotBetweenThan (
    @NonNull final List<String> errors, 
    @NonNull final String result
  ) {
    this.validateBetween(errors, result.substring(1));
  }

  private void validateBetween (
    @NonNull final List<String> errors,
    @NonNull final String result
  ) {
    final Matcher matcher = DateTimeFilterParser.BETWEEN_PATTERN.matcher(result);
    matcher.matches();
    matcher.groupCount();
    
    final String dateFormat = matcher.group("dateFormat");
    final String firstDateValue = matcher.group("firstDateValue");
    final String secondDateValue = matcher.group("secondDateValue");

    final String pattern = (dateFormat == null) ? DateTimeFilterParser.DEFAULT_DATE_FORMAT_PATTERN : dateFormat;
    
    this.validateParsedDate(errors, pattern, firstDateValue);
    this.validateParsedDate(errors, pattern, secondDateValue);
  }

  private void validateLessThanOrEqualTo (
    @NonNull final List<String> errors,
    @NonNull final String result
  ) {
    validateDate(errors, result.split("lte:")[1]);
  }

  private void validateLessThan (
    @NonNull final List<String> errors,
    @NonNull final String result
  ) {
    validateDate(errors, result.split("lt:")[1]);
  }

  private void validateGreaterThanOrEqualTo (
    @NonNull final List<String> errors,
    @NonNull final String result
  ) {
    validateDate(errors, result.split("gte:")[1]);
  }

  private void validateGreaterThan (
    @NonNull final List<String> errors,
    @NonNull final String result
  ) {
    validateDate(errors, result.split("gt:")[1]);
  }
  
  private void validateDate (
    @NonNull final List<String> errors,
    @NonNull final String date
  ) {
    final Matcher matcher = DateTimeFilterParser.DATE_PATTERN.matcher(date);
    matcher.matches();
    matcher.groupCount();

    final String standardDate = matcher.group("standardDate");
    final String dateFormat = matcher.group("dateFormat");
    final String dateValue = matcher.group("dateValue");
    
    if (standardDate != null) {
      validateParsedDate(errors, DateTimeFilterParser.DEFAULT_DATE_FORMAT_PATTERN, standardDate);
    } else {
      validateParsedDate(errors, dateFormat, dateValue);
    }
  }

  private void validateParsedDate (
    @NonNull final List<String> errors,
    @NonNull final String pattern,
    @NonNull final String date
  ) {
   DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
    
    try {
      format.parse(date);
    } catch (final DateTimeParseException e) {
      errors.add(createInvalidDateValueError(date, pattern));
    } catch (final IllegalArgumentException e) {
      errors.add(createInvalidDateFormatError(pattern));
    }
  }

  private String createInvalidDateFormatError (@NonNull final String dateFormat) {
    return String.join(
      "",
      "The custom date format \"", dateFormat, "\" is not a valid DateTime format",
      " see java.time.format.DateTimeFormatter documentation for more information about",
      " this error."
    );
  }

  private String createInvalidDateValueError (
    @NonNull final String dateValue,
    @NonNull final String dateFormat
  ) {
    return String.join(
      "",
      "The given date value \"", dateValue, "\" does not match the given format",
      " \"", dateFormat, "\" see java.time.format.DateTimeFormatter documentation for more information about" + 
      " this error."
    );
  }
}
