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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.liara.api.filter.ast.ConjunctionFilterNode;
import org.liara.api.filter.ast.DisjunctionFilterNode;
import org.liara.api.filter.ast.LikeFilterNode;
import org.liara.api.filter.ast.PredicateFilterNode;
import org.liara.api.filter.ast.RegexpFilterNode;
import org.liara.api.filter.ast.ValueFilterNode;
import org.springframework.lang.NonNull;

/**
 * @author Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 *
 */
public class TextFilterParser implements FilterParser
{
  @NonNull
  public static final Pattern           PREDICATE_PATTERN           = Pattern.compile(
    String.join(
      "|",
      "(?<regexp>\\/([^\\/\\\\]|(\\\\.))+\\/)",
      "(?<containsExactly>\"([^\"\\\\]|(\\\\.))+\")",
      "(?<disjunction>;)",
      "(?<contains>[^\\s\"\\/]+)"
    )
  );

  public PredicateFilterNode parse (@NonNull final String value) {
    final Matcher matcher = PREDICATE_PATTERN.matcher(value);
    final List<PredicateFilterNode> result = new ArrayList<>();
    final List<PredicateFilterNode> current = new ArrayList<>();
    
    while (matcher.find()) {
      String token = null;
      
      if ((token = matcher.group("regexp")) != null) {
        current.add(parseRegexp(token));
      } else if ((token = matcher.group("containsExactly")) != null) {
        current.add(parseExactly(token));
      } else if ((token = matcher.group("disjunction")) != null) {
        result.add(new ConjunctionFilterNode(current));
        current.clear();
      } else {
        current.add(parseContains(matcher.group("contains")));
      }
    }
    
    if (current.size() > 0) {
      result.add(new ConjunctionFilterNode(current));
      current.clear();
    }
    
    return new DisjunctionFilterNode(result);
  }

  private PredicateFilterNode parseContains (@NonNull final String token) {
    return new LikeFilterNode(ValueFilterNode.from("%" + token + "%"));
  }

  private PredicateFilterNode parseExactly (String token) {
    return new LikeFilterNode(ValueFilterNode.from("%" + token.substring(1, token.length() - 1).replace("\\\"", "\"") + "%"));
  }

  private PredicateFilterNode parseRegexp (String token) {
    return new RegexpFilterNode(ValueFilterNode.from(token.substring(1, token.length() - 1).replace("\\/", "/")));
  }
}
