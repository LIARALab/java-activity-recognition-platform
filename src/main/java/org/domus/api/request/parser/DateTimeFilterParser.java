/**
 * Copyright 2018 Cédric DEMONGIVERT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.domus.api.request.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.domus.api.date.PartialLocalDateTime;
import org.domus.api.filter.BetweenFilter;
import org.domus.api.filter.ConjunctionFilter;
import org.domus.api.filter.DisjunctionFilter;
import org.domus.api.filter.EqualFilter;
import org.domus.api.filter.Filter;
import org.domus.api.filter.GreaterThanFilter;
import org.domus.api.filter.GreaterThanOrEqualToFilter;
import org.domus.api.filter.LessThanFilter;
import org.domus.api.filter.LessThanOrEqualToFilter;
import org.domus.api.filter.NotFilter;
import org.domus.api.filter.TemporalBetweenFilter;
import org.domus.api.filter.TemporalEqualFilter;
import org.domus.api.filter.TemporalGreaterThanFilter;
import org.domus.api.filter.TemporalGreaterThanOrEqualToFilter;
import org.domus.api.filter.TemporalLessThanFilter;
import org.domus.api.filter.TemporalLessThanOrEqualToFilter;
import org.domus.api.request.APIRequest;
import org.domus.api.request.APIRequestParameter;
import org.springframework.lang.NonNull;

/**
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 */
public class DateTimeFilterParser implements FilterParser<LocalDateTime>
{
  @NonNull
  public static final String            DEFAULT_DATE_FORMAT_PATTERN = "uuuu-MM-dd'T'HH:mm:ss.SSS";

  @NonNull
  public static final DateTimeFormatter DEFAULT_DATE_FORMAT         = DateTimeFormatter
    .ofPattern(DEFAULT_DATE_FORMAT_PATTERN);

  @NonNull
  public static final Pattern           DATE_PATTERN                = Pattern
    .compile("((\\[(?<dateFormat>[^\\]]*?)\\]\\[(?<dateValue>[^\\]]*?)\\])|\\[(?<standardDate>[^\\]]*?)\\])");

  @NonNull
  public static final Pattern           UNGROUPED_DATE_PATTERN      = Pattern
    .compile(DATE_PATTERN.pattern().replaceAll("\\(\\?\\<[a-zA-Z]*?\\>", "("));

  @NonNull
  public static final Pattern           BETWEEN_PATTERN             = Pattern
    .compile("((\\[(?<dateFormat>[^\\]]*?)\\])?\\[(?<firstDateValue>[^\\]]*?)\\]\\:\\[(?<secondDateValue>[^\\]]*?)\\])");
  
  @NonNull
  public static final Pattern UNGROUPED_BETWEEN_PATTERN = Pattern.compile(
    BETWEEN_PATTERN.pattern().replaceAll("\\(\\?\\<[a-zA-Z]*?\\>", "(")
  );

  @NonNull
  public static final Pattern           PREDICATE_PATTERN           = Pattern.compile(
    String.join(
      "|",
      "(?<greaterThan>gt:" + UNGROUPED_DATE_PATTERN.pattern() + ")",
      "(?<greaterThanOrEqualTo>gte:" + UNGROUPED_DATE_PATTERN.pattern() + ")",
      "(?<lessThan>lt:" + UNGROUPED_DATE_PATTERN.pattern() + ")",
      "(?<lessThanOrEqualTo>lte:" + UNGROUPED_DATE_PATTERN.pattern() + ")",
      "(?<notBetween>\\!" + UNGROUPED_BETWEEN_PATTERN.pattern() + ")",
      "(?<between>" + UNGROUPED_BETWEEN_PATTERN.pattern() + ")",
      "(?<notEqualTo>\\!" + UNGROUPED_DATE_PATTERN.pattern() + ")",
      "(?<equalTo>" + UNGROUPED_DATE_PATTERN.pattern() + ")"
    )
  );

  @NonNull
  private final String                  _parameter;

  public DateTimeFilterParser(@NonNull final String parameter) {
    _parameter = parameter;
  }

  @Override
  public Filter<LocalDateTime> parse (@NonNull final APIRequest request) {
    final List<Filter<LocalDateTime>> specs = new ArrayList<>();

    if (request.contains(_parameter)) {
      final APIRequestParameter parameter = request.getParameter(_parameter);
      for (final String token : parameter) {
        specs.add(this.parseFilter(token.trim()));
      }
    }

    return new DisjunctionFilter<LocalDateTime>(specs);
  }

  private Filter<LocalDateTime> parseFilter (@NonNull final String value) {
    return new DisjunctionFilter<LocalDateTime>(
      Arrays.stream(value.split(";")).map(token -> parseConjunction(token.trim())).toArray(size -> (Filter<LocalDateTime>[]) new Filter[size])
    );
  }

  private Filter<LocalDateTime> parseConjunction (@NonNull final String value) {
    return new ConjunctionFilter<LocalDateTime>(
      Arrays.stream(value.split(",")).map(token -> parsePredicate(token.trim())).toArray(size -> (Filter<LocalDateTime>[]) new Filter[size])
    );
  }

  private Filter<LocalDateTime> parsePredicate (@NonNull final String value) {
    final Matcher matcher = PREDICATE_PATTERN.matcher(value);
    matcher.matches();
    matcher.groupCount();

    String result = null;

    if ((result = matcher.group("greaterThan")) != null) {
      return this.parseGreaterThan(result);
    } else if ((result = matcher.group("greaterThanOrEqualTo")) != null) {
      return this.parseGreaterThanOrEqualTo(result);
    } else if ((result = matcher.group("lessThan")) != null) {
      return this.parseLessThan(result);
    } else if ((result = matcher.group("lessThanOrEqualTo")) != null) {
      return this.parseLessThanOrEqualTo(result);
    } else if ((result = matcher.group("between")) != null) {
      return this.parseBetween(result);
    } else if ((result = matcher.group("notBetween")) != null) {
      return this.parseNotBetweenThan(result);
    } else if ((result = matcher.group("equalTo")) != null) {
      return this.parseEqualTo(result);
    } else {
      return this.parseNotEqualTo(matcher.group("notEqualTo"));
    }
  }

  private Filter<LocalDateTime> parseNotEqualTo (@NonNull final String result) {
    return new NotFilter<LocalDateTime>(this.parseEqualTo(result.substring(1)));
  }

  private Filter<LocalDateTime> parseEqualTo (@NonNull final String result) {
    return new TemporalEqualFilter(this.parseDate(result));
  }

  private Filter<LocalDateTime> parseNotBetweenThan (@NonNull final String result) {
    return new NotFilter<LocalDateTime>(this.parseBetween(result.substring(1)));
  }

  private Filter<LocalDateTime> parseBetween (@NonNull final String result) {
    final PartialLocalDateTime[] dates = this.parseBetweenDates(result);

    return new TemporalBetweenFilter(dates[0], dates[1]);
  }

  private Filter<LocalDateTime> parseLessThanOrEqualTo (@NonNull final String result) {
    return new TemporalLessThanOrEqualToFilter(parseDate(result.split("lte:")[1]));
  }

  private Filter<LocalDateTime> parseLessThan (@NonNull final String result) {
    return new TemporalLessThanFilter(parseDate(result.split("lt:")[1]));
  }

  private Filter<LocalDateTime> parseGreaterThanOrEqualTo (@NonNull final String result) {
    return new TemporalGreaterThanOrEqualToFilter(parseDate(result.split("gte:")[1]));
  }

  private Filter<LocalDateTime> parseGreaterThan (@NonNull final String result) {
    return new TemporalGreaterThanFilter(parseDate(result.split("gt:")[1]));
  }

  private PartialLocalDateTime[] parseBetweenDates (@NonNull final String result) {
    final Matcher matcher = BETWEEN_PATTERN.matcher(result);
    matcher.matches();
    matcher.groupCount();
    
    final String dateFormat = matcher.group("dateFormat");
    final String firstDateValue = matcher.group("firstDateValue");
    final String secondDateValue = matcher.group("secondDateValue");

    final DateTimeFormatter format = (dateFormat == null) ? DEFAULT_DATE_FORMAT : DateTimeFormatter.ofPattern(dateFormat);
    
    return new PartialLocalDateTime[] {
      this.parsePartialDate(format, firstDateValue),
      this.parsePartialDate(format, secondDateValue)
    };
  }
  
  private PartialLocalDateTime parseDate (@NonNull final String result) {
    final Matcher matcher = DATE_PATTERN.matcher(result);
    matcher.matches();
    matcher.groupCount();
    
    final String standardDate = matcher.group("standardDate");
    final String dateFormat = matcher.group("dateFormat");
    final String dateValue = matcher.group("dateValue");

    if (standardDate != null) {
      return this.parsePartialDate(DEFAULT_DATE_FORMAT, standardDate);
    } else {
      return this.parsePartialDate(
        DateTimeFormatter.ofPattern(dateFormat),
        dateValue
      );
    }
  }

  private PartialLocalDateTime parsePartialDate (@NonNull final DateTimeFormatter format, @NonNull final String date) {    
    return format.parse(date, PartialLocalDateTime::from);
  }
}
