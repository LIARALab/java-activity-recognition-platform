package org.liara.api.request.parser.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.liara.api.collection.transformation.operator.EntityCollectionConjunctionOperator;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.request.APIRequest;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

public class APIRequestEntityCollectionConjunctionOperatorParser<Entity>
  implements APIRequestEntityCollectionOperatorParser<Entity>
{
  @NonNull
  private final List<APIRequestEntityCollectionOperatorParser<Entity>> _parsers = new ArrayList<>();
  
  public APIRequestEntityCollectionConjunctionOperatorParser(
    @NonNull final Iterable<APIRequestEntityCollectionOperatorParser<Entity>> parsers
  ) {
    Iterables.addAll(_parsers, parsers);
  }
  
  public APIRequestEntityCollectionConjunctionOperatorParser(
    @NonNull final Iterator<APIRequestEntityCollectionOperatorParser<Entity>> parsers
  ) {
    Iterators.addAll(_parsers, parsers);
  }
  
  public APIRequestEntityCollectionConjunctionOperatorParser(
    @NonNull final APIRequestEntityCollectionOperatorParser<Entity>[] parsers
  ) {
    _parsers.addAll(Arrays.asList(parsers));
  }

  @Override
  public EntityCollectionOperator<Entity> parse (@NonNull final APIRequest request) {
    return new EntityCollectionConjunctionOperator<>(
      _parsers.stream()
              .map(x -> x.parse(request))
              .iterator()
    );
  }
}
