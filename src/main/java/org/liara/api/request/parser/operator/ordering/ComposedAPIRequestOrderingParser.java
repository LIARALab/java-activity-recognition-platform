package org.liara.api.request.parser.operator.ordering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.liara.api.collection.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.operator.EntityCollectionIdentityOperator;
import org.liara.api.collection.operator.EntityCollectionOperator;
import org.liara.api.collection.operator.EntityCollectionOrderingOperator;
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
  
  public ComposedAPIRequestOrderingParser (@NonNull final APIRequestOrderingProcessor<Entity>... parsers) {
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
