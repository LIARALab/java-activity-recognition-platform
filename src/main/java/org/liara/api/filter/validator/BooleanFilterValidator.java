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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.liara.api.filter.parser.BooleanFilterParser;
import org.springframework.lang.NonNull;

public class BooleanFilterValidator implements FilterValidator
{
  @NonNull
  public static final Pattern PREDICATE_PATTERN = Pattern.compile(
    String.join(
      "|",
      "^(" + BooleanFilterParser.TRUE_PATTERN.pattern() + ")",
      "(" + BooleanFilterParser.FALSE_PATTERN.pattern() + ")",
      "(" + BooleanFilterParser.FALSE_PATTERN.pattern() + ";" + BooleanFilterParser.TRUE_PATTERN.pattern() + ")",
      "(" + BooleanFilterParser.TRUE_PATTERN.pattern() + ";" + BooleanFilterParser.FALSE_PATTERN.pattern() + ")",
      "(\\s*)$"
    )
  );

  @Override
  public String getBestMatchPattern () {
    return PREDICATE_PATTERN.pattern();
  }

  public List<String> validate (@NonNull final String value)
  {
    if (!PREDICATE_PATTERN.matcher(value).find()) {
      return Arrays.asList(
        String.join(
          "\\r\\n",
          "The given value does not match the boolean filter structure :",
          "",
          "boolean-filter: (trueValue;falseValue)|(falseValue;trueValue)|(trueValue)|(falseValue)|\\s*",
          "",
          "trueValue: 1|true",
          "",
          "falseValue: 0|false"
        )
      );
    } else {
      return Collections.emptyList();
    }
  }
}
