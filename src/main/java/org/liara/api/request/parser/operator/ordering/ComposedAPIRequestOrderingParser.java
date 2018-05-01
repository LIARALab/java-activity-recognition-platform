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
package org.liara.api.request.parser.operator.ordering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator;
import org.liara.api.request.APIRequest;
import org.liara.api.request.APIRequestParameter;
import org.liara.api.request.parser.operator.APIRequestEntityCollectionOperatorParser;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class      ComposedAPIRequestOrderingParser<Entity> 
       implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  public static final Pattern ORDERING_PATTERN = Pattern.compile("((.*?)\\:(asc|desc))");
  
  @NonNull
  public static final Pattern FULL_ORDERING_PATTERN = Pattern.compile(
    String.join("", "^", ORDERING_PATTERN.pattern(), "(,", ORDERING_PATTERN.pattern() + ")*$")
  );
  
  @NonNull
  private final Set<APIRequestOrderingProcessor<Entity>> _parsers = new HashSet<>(); 

  public ComposedAPIRequestOrderingParser () {

  }
  
  public ComposedAPIRequestOrderingParser (@NonNull final Iterable<APIRequestOrderingProcessor<Entity>> parsers) {
    Iterables.addAll(_parsers, parsers);
  }
  
  public ComposedAPIRequestOrderingParser (@NonNull final Iterator<APIRequestOrderingProcessor<Entity>> parsers) {
    Iterators.addAll(_parsers, parsers);
  }
  
  public ComposedAPIRequestOrderingParser (@NonNull final APIRequestOrderingProcessor<Entity>[] parsers) {
    _parsers.addAll(Arrays.asList(parsers));
  }
  
  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    if (request.contains("orderBy")) {
      final List<String[]> orderingRequest = parseOrderBy(request);
      final List<EntityCollectionOperator<Entity>> orderings = new ArrayList<>();
      
      for (final String[] pair : orderingRequest) {
        final String key = pair[0];
        
        final EntityCollectionOrderingOperator.Direction direction = (
            pair[1].equals("asc")
        ) ? EntityCollectionOrderingOperator.Direction.ASC 
          : EntityCollectionOrderingOperator.Direction.DESC;
        
        for (final APIRequestOrderingProcessor<Entity> parser : _parsers) {
          if (parser.hableToProcess(key, direction)) {
            orderings.add(parser.process(key, direction));
          }
        }
      }
      
      return new EntityCollectionConjunctionOperator<>(orderings);
    } else {
      return new EntityCollectionIdentityOperator<>();
    }
  }
  
  private List<String[]> parseOrderBy (@NonNull final APIRequest request) {
    final List<String[]> result = new ArrayList<>();
    final APIRequestParameter parameter = request.getParameter("orderBy");
    
    for (final String value : parameter) {
      parserOrderByValue(value, result);
    }
    
    return result;
  }
  
  private void parserOrderByValue (@NonNull final String token, @NonNull final List<String[]> result) {
    for (final String pair : token.split(",")) {
      final String[] pairToken = pair.split(":");
      
      result.add(
        new String[] {
          pairToken[0].trim(), 
          pairToken[1].toLowerCase().trim()
        }
      );
    }
  }
}
