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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.domus.api.request.APIRequest;
import org.domus.api.request.APIRequestParameter;
import org.springframework.lang.NonNull;

/**
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 */
public class IntegerFilterParser implements FilterParser<Integer>
{
  @NonNull
  public static final Pattern NUMBER_PATTERN    = Pattern.compile("((\\+|\\-)?\\d+)");

  @NonNull
  public static final Pattern PREDICATE_PATTERN = Pattern.compile(
    String.join(
      "|",
      "(?<greaterThan>gt:" + NUMBER_PATTERN.pattern() + ")",
      "(?<greaterThanOrEqualTo>gte:" + NUMBER_PATTERN.pattern() + ")",
      "(?<lessThan>lt:" + NUMBER_PATTERN.pattern() + ")",
      "(?<lessThanOrEqualTo>lte:" + NUMBER_PATTERN.pattern() + ")",
      "(?<between>" + NUMBER_PATTERN.pattern() + "\\:" + NUMBER_PATTERN.pattern() + ")",
      "(?<notBetween>\\!" + NUMBER_PATTERN.pattern() + "\\:" + NUMBER_PATTERN.pattern() + ")",
      "(?<equalTo>" + NUMBER_PATTERN.pattern() + ")",
      "(?<notEqualTo>\\!" + NUMBER_PATTERN.pattern() + ")"
    )
  );

  @NonNull
  private final String        _parameter;

  public IntegerFilterParser(@NonNull final String parameter) {
    _parameter = parameter;
  }

  @Override
  public Filter<Integer> parse (@NonNull final APIRequest request) {
    final List<Filter<Integer>> specs = new ArrayList<>();

    if (request.contains(_parameter)) {
      final APIRequestParameter parameter = request.getParameter(_parameter);
      for (final String token : parameter) {
        specs.add(this.parseFilter(token.trim()));
      }
    }

    return new DisjunctionFilter<Integer>(specs);
  }

  private Filter<Integer> parseFilter (@NonNull final String value) {
    return new DisjunctionFilter<Integer>(
      Arrays.stream(value.split(";")).map(token -> parseConjunction(token.trim())).toArray(size -> (Filter<Integer>[]) new Filter[size])
    );
  }

  private Filter<Integer> parseConjunction (@NonNull final String value) {
    return new ConjunctionFilter<Integer>(
      Arrays.stream(value.split(",")).map(token -> parsePredicate(token.trim())).toArray(size -> (Filter<Integer>[]) new Filter[size])
    );
  }

  private Filter<Integer> parsePredicate (@NonNull final String value) {
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
      return this.parseBetweenThan(result);
    } else if ((result = matcher.group("notBetween")) != null) {
      return this.parseNotBetweenThan(result);
    } else if ((result = matcher.group("equalTo")) != null) {
      return this.parseEqualTo(result);
    } else {
      return this.parseNotEqualTo(matcher.group("notEqualTo"));
    }
  }

  private Filter<Integer> parseNotEqualTo (@NonNull final String result) {
    return new NotFilter<Integer>(this.parseEqualTo(result.substring(1)));
  }

  private Filter<Integer> parseEqualTo (@NonNull final String result) {
    return new EqualFilter<Integer>(Integer.parseInt(result));
  }

  private Filter<Integer> parseNotBetweenThan (@NonNull final String result) {
    return new NotFilter<Integer>(this.parseBetweenThan(result.substring(1)));
  }

  private Filter<Integer> parseBetweenThan (@NonNull final String result) {
    final String[] tokens = result.split(":");

    return new BetweenFilter<Integer>(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
  }

  private Filter<Integer> parseLessThanOrEqualTo (@NonNull final String result) {
    return new LessThanOrEqualToFilter<Integer>(Integer.parseInt(result.split(":")[1]));
  }

  private Filter<Integer> parseLessThan (@NonNull final String result) {
    return new LessThanFilter<Integer>(Integer.parseInt(result.split(":")[1]));
  }

  private Filter<Integer> parseGreaterThanOrEqualTo (@NonNull final String result) {
    return new GreaterThanOrEqualToFilter<Integer>(Integer.parseInt(result.split(":")[1]));
  }

  private Filter<Integer> parseGreaterThan (@NonNull final String result) {
    return new GreaterThanFilter<Integer>(Integer.parseInt(result.split(":")[1]));
  }
}
