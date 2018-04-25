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
public class IntegerFilterParser implements FilterParser
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
      return this.parseBetweenThan(result);
    } else if ((result = matcher.group("notBetween")) != null) {
      return this.parseNotBetweenThan(result);
    } else if ((result = matcher.group("equalTo")) != null) {
      return this.parseEqualTo(result);
    } else {
      return this.parseNotEqualTo(matcher.group("notEqualTo"));
    }
  }

  private PredicateFilterNode parseNotEqualTo (@NonNull final String result) {
    return new NotFilterNode(this.parseEqualTo(result.substring(1)));
  }

  private PredicateFilterNode parseEqualTo (@NonNull final String result) {
    return new EqualToFilterNode<Integer>(parseInteger(result));
  }

  private PredicateFilterNode parseNotBetweenThan (@NonNull final String result) {
    return new NotFilterNode(this.parseBetweenThan(result.substring(1)));
  }

  private PredicateFilterNode parseBetweenThan (@NonNull final String result) {
    final String[] tokens = result.split(":");
    return new BetweenFilterNode<Integer>(parseInteger(tokens[0]), parseInteger(tokens[1]));
  }

  private PredicateFilterNode parseLessThanOrEqualTo (@NonNull final String result) {
    return new LessThanOrEqualToFilterNode<Integer>(parseInteger(result.split(":")[1]));
  }

  private PredicateFilterNode parseLessThan (@NonNull final String result) {
    return new LessThanFilterNode<Integer>(parseInteger(result.split(":")[1]));
  }

  private PredicateFilterNode parseGreaterThanOrEqualTo (@NonNull final String result) {
    return new GreaterThanOrEqualToFilterNode<Integer>(parseInteger(result.split(":")[1]));
  }

  private PredicateFilterNode parseGreaterThan (@NonNull final String result) {
    return new GreaterThanFilterNode<Integer>(parseInteger(result.split(":")[1]));
  }
  
  private ValueFilterNode<Integer> parseInteger (@NonNull final String token) {
    return ValueFilterNode.from(Integer.parseInt(token));
  }
}
