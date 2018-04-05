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
package org.liara.api.request.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.liara.api.collection.filtering.EntityCollectionFilter;
import org.liara.api.collection.filtering.EntityFieldFilter;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * @author Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
 */
public class APIRequestEntityCollectionFilterParser<Entity> implements APIRequestParser<EntityCollectionFilter<Entity>>
{
  @NonNull
  private final Set<APIRequestEntityFieldFilterParser<Entity, ?>> _parsers = new HashSet<>();

  public APIRequestEntityCollectionFilterParser () {

  }
  
  public APIRequestEntityCollectionFilterParser (@NonNull final Iterable<APIRequestEntityFieldFilterParser<Entity, ?>> parsers) {
    Iterables.addAll(_parsers, parsers);
  }
  
  public APIRequestEntityCollectionFilterParser (@NonNull final Iterator<APIRequestEntityFieldFilterParser<Entity, ?>> parsers) {
    Iterators.addAll(_parsers, parsers);
  }
  
  public APIRequestEntityCollectionFilterParser (@NonNull final APIRequestEntityFieldFilterParser<Entity, ?>... parsers) {
    _parsers.addAll(Arrays.asList(parsers));
  }

  @Override
  public EntityCollectionFilter<Entity> parse (@NonNull final APIRequest request) {
    final List<EntityFieldFilter<Entity, ?>> filters = new ArrayList<>();
    
    for (final APIRequestEntityFieldFilterParser<Entity, ?> parser : _parsers) {
      final EntityFieldFilter<Entity, ?> filter = parser.parse(request);
      
      if (filter != null) {
        filters.add(filter);
      }
    }
    
    return new EntityCollectionFilter<Entity>(filters);
  }
}
