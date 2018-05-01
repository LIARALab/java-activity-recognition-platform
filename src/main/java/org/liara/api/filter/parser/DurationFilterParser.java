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
/**
 * Copyright 2018 Cedric DEMONGIVERT
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
package org.liara.api.filter.parser;

import java.time.Duration;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.liara.api.filter.ast.BetweenFilterNode;
import org.liara.api.filter.ast.ConjunctionFilterNode;
import org.liara.api.filter.ast.DisjunctionFilterNode;
import org.liara.api.filter.ast.EqualToFilterNode;
import org.liara.api.filter.ast.GreaterThanFilterNode;
import org.liara.api.filter.ast.GreaterThanOrEqualToFilterNode;
import org.liara.api.filter.ast.LessThanFilterNode;
import org.liara.api.filter.ast.LessThanOrEqualToFilterNode;
import org.liara.api.filter.ast.NotFilterNode;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.liara.api.filter.ast.ValueFilterNode;
import org.springframework.lang.NonNull;

/**
 * @author Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 */
public class DurationFilterParser implements FilterParser
{
  @NonNull
  public static final Pattern INTEGER_PATTERN = Pattern.compile("(\\+|\\-)?\\d+");
  
  @NonNull
  public static final Pattern           DURATION_TOKEN_PATTERN                = Pattern
    .compile("(" + String.join(
      "|",
      "(?<weeks>" + INTEGER_PATTERN.pattern() + "(w|week|weeks))",
      "(?<days>" + INTEGER_PATTERN.pattern() + "(d|day|days))",
      "(?<hours>" + INTEGER_PATTERN.pattern() + "(h|hour|hours))",
      "(?<minutes>" + INTEGER_PATTERN.pattern() + "(m|minute|minutes))",
      "(?<milliseconds>" + INTEGER_PATTERN.pattern() + "(S|milliseconds|milliseconds))",
      "(?<seconds>" + INTEGER_PATTERN.pattern() + "(s|second|seconds)?)"
    ) + ")");

  @NonNull
  public static final Pattern           UNGROUPED_DURATION_TOKEN_PATTERN      = Pattern
    .compile(DURATION_TOKEN_PATTERN .pattern().replaceAll("\\(\\?\\<[a-zA-Z]*?\\>", "("));
  
  @NonNull
  public static final Pattern           DURATION_PATTERN                = Pattern
    .compile("(" + UNGROUPED_DURATION_TOKEN_PATTERN.pattern() + "+)");

  @NonNull
  public static final Pattern           PREDICATE_PATTERN           = Pattern.compile(
    String.join(
      "|",
      "(?<greaterThan>gt:" + DURATION_PATTERN.pattern() + ")",
      "(?<greaterThanOrEqualTo>gte:" + DURATION_PATTERN.pattern() + ")",
      "(?<lessThan>lt:" + DURATION_PATTERN.pattern() + ")",
      "(?<lessThanOrEqualTo>lte:" + DURATION_PATTERN.pattern() + ")",
      "(?<notBetween>\\!" + DURATION_PATTERN.pattern() + ":" + DURATION_PATTERN.pattern() + ")",
      "(?<between>" + DURATION_PATTERN.pattern() + ":" + DURATION_PATTERN.pattern() + ")",
      "(?<notEqualTo>\\!" + DURATION_PATTERN.pattern() + ")",
      "(?<equalTo>" + DURATION_PATTERN.pattern() + ")"
    )
  );

  public PredicateFilterNode parse (@NonNull final String value) {
    return new DisjunctionFilterNode(
      Arrays.stream(value.split(";"))
            .map(token -> parseConjunction(token.trim()))
            .iterator()
    );
  }

  private PredicateFilterNode parseConjunction (@NonNull final String value) {
    return new ConjunctionFilterNode(
      Arrays.stream(value.split(","))
            .map(token -> parsePredicate(token.trim()))
            .iterator()
    );
  }

  private PredicateFilterNode parsePredicate (@NonNull final String value) {
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

  private PredicateFilterNode parseNotEqualTo (@NonNull final String result) {
    return new NotFilterNode(parseEqualTo(result.substring(1)));
  }

  private PredicateFilterNode parseEqualTo (@NonNull final String result) {
    return new EqualToFilterNode<>(parseDuration(result));
  }

  private PredicateFilterNode parseNotBetweenThan (@NonNull final String result) {
    return new NotFilterNode(parseBetween(result.substring(1)));
  }

  private PredicateFilterNode parseBetween (@NonNull final String result) {
    final String[] tokens = result.split(":");

    return new BetweenFilterNode<>(parseDuration(tokens[0]), parseDuration(tokens[1]));
  }

  private PredicateFilterNode parseLessThanOrEqualTo (@NonNull final String result) {
    return new LessThanOrEqualToFilterNode<>(parseDuration(result.split("lte:")[1]));
  }

  private PredicateFilterNode parseLessThan (@NonNull final String result) {
    return new LessThanFilterNode<>(parseDuration(result.split("lt:")[1]));
  }

  private PredicateFilterNode parseGreaterThanOrEqualTo (@NonNull final String result) {
    return new GreaterThanOrEqualToFilterNode<>(parseDuration(result.split("gte:")[1]));
  }

  private PredicateFilterNode parseGreaterThan (@NonNull final String result) {
    return new GreaterThanFilterNode<>(parseDuration(result.split("gt:")[1]));
  }
  
  private ValueFilterNode<Long> parseDuration (@NonNull final String duration) {
    final Matcher matcher = DURATION_TOKEN_PATTERN.matcher(duration);
    Duration result = Duration.ofSeconds(0);
    
    while (matcher.find()) {
      String token = null;
      
      if ((token = matcher.group("weeks")) != null) {
        result = result.plus(Duration.ofDays(7 * parseTokenValue(token)));
      } else if ((token = matcher.group("days")) != null) {
        result = result.plus(Duration.ofDays(parseTokenValue(token)));
      } else if ((token = matcher.group("hours")) != null) {
        result = result.plus(Duration.ofHours(parseTokenValue(token)));
      } else if ((token = matcher.group("minutes")) != null) {
        result = result.plus(Duration.ofMinutes(parseTokenValue(token)));
      } else if ((token = matcher.group("milliseconds")) != null) {
        result = result.plus(Duration.ofMillis(parseTokenValue(token)));
      } else {
        result = result.plus(Duration.ofSeconds(parseTokenValue(matcher.group("seconds"))));
      }
    }
    
    return ValueFilterNode.from(result.getSeconds() * 1000L + result.getNano() / 1000000L);
  }
  
  private long parseTokenValue (@NonNull final String token) {
    final Matcher matcher = INTEGER_PATTERN.matcher(token);
    matcher.find();
    
    return Long.parseLong(token.substring(matcher.start(), matcher.end()));
  }
}
