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
package org.liara.api.request.parser.transformation.grouping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.liara.api.collection.transformation.grouping.EntityCollectionGroupTransformation;
import org.liara.api.collection.transformation.grouping.EntityCollectionMultipleGroupingTransformation;
import org.liara.api.request.APIRequest;
import org.liara.api.request.APIRequestParameter;
import org.liara.api.request.parser.APIRequestParser;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class      ComposedAPIRequestGroupingParser<Entity> 
       implements APIRequestParser<EntityCollectionGroupTransformation<Entity>> 
{  
  @NonNull
  private final Set<APIRequestGroupingProcessor<Entity>> _parsers = new HashSet<>(); 

  public ComposedAPIRequestGroupingParser () {

  }
  
  public ComposedAPIRequestGroupingParser (
    @NonNull final Iterable<APIRequestGroupingProcessor<Entity>> parsers
  ) {
    Iterables.addAll(_parsers, parsers);
  }
  
  public ComposedAPIRequestGroupingParser (
    @NonNull final Iterator<APIRequestGroupingProcessor<Entity>> parsers
  ) {
    Iterators.addAll(_parsers, parsers);
  }
  
  public ComposedAPIRequestGroupingParser (
    @NonNull final APIRequestGroupingProcessor<Entity>[] parsers
  ) {
    _parsers.addAll(Arrays.asList(parsers));
  }
  
  @Override
  public EntityCollectionGroupTransformation<Entity> parse (@NonNull final APIRequest request) {
    if (request.contains("groupBy")) {
      final List<String> groupingRequest = parseGroupBy(request);
      final List<EntityCollectionGroupTransformation<Entity>> groupings = new ArrayList<>();
      
      for (final String key : groupingRequest) {
        for (final APIRequestGroupingProcessor<Entity> parser : _parsers) {
          if (parser.hableToProcess(key)) {
            groupings.add(parser.process(key));
          }
        }
      }
      
      return new EntityCollectionMultipleGroupingTransformation<>(groupings);
    } else {
      return null;
    }
  }
  
  private List<String> parseGroupBy (@NonNull final APIRequest request) {
    final List<String> result = new ArrayList<>();
    final APIRequestParameter parameter = request.getParameter("groupBy");
    
    for (final String value : parameter) {
      Arrays.stream(value.trim().split(","))
            .map(x -> x.trim())
            .forEach(x -> result.add(x));
    }
    
    return result;
  }
}
