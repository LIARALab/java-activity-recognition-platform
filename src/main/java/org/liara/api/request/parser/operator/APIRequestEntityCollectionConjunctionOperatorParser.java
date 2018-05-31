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
package org.liara.api.request.parser.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.APIRequest;
import org.liara.api.request.parser.APIDocumentedRequestParser;
import org.springframework.lang.NonNull;

import com.google.common.collect.ImmutableList;

import springfox.documentation.service.Parameter;

public class APIRequestEntityCollectionConjunctionOperatorParser<Entity>
  implements APIRequestEntityCollectionOperatorParser<Entity>, APIDocumentedRequestParser
{
  @NonNull
  private final List<APIRequestEntityCollectionOperatorParser<Entity>> _parsers;
  
  public APIRequestEntityCollectionConjunctionOperatorParser(
    @NonNull final Iterable<APIRequestEntityCollectionOperatorParser<Entity>> parsers
  ) {
    _parsers = ImmutableList.copyOf(parsers);
  }
  
  public APIRequestEntityCollectionConjunctionOperatorParser(
    @NonNull final Iterator<APIRequestEntityCollectionOperatorParser<Entity>> parsers
  ) {
    _parsers = ImmutableList.copyOf(parsers);
  }
  
  public APIRequestEntityCollectionConjunctionOperatorParser(
    @NonNull final APIRequestEntityCollectionOperatorParser<Entity>[] parsers
  ) {
    _parsers = ImmutableList.copyOf(Arrays.asList(parsers));
  }

  public APIRequestEntityCollectionConjunctionOperatorParser() { 
    _parsers = ImmutableList.of();
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    return new EntityCollectionConjunctionOperator<>(
      _parsers.stream()
              .map(x -> x.parse(request))
              .iterator()
    );
  }
  
  public List<APIRequestEntityCollectionOperatorParser<Entity>> getParsers () {
    return _parsers;
  }

  @Override
  public List<Parameter> getHandledParametersDocumentation (@NonNull final List<APIDocumentedRequestParser> parents) {
    final List<Parameter> result = new LinkedList<>();
    final List<APIDocumentedRequestParser> nextParents = new ArrayList<>(parents);
    nextParents.add(this);
    
    for (final APIRequestEntityCollectionOperatorParser<Entity> parser : _parsers) {
      if (parser instanceof APIDocumentedRequestParser) {
        result.addAll(((APIDocumentedRequestParser) parser).getHandledParametersDocumentation(nextParents));
      }
    }
    
    return result;
  }
}
