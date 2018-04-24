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
