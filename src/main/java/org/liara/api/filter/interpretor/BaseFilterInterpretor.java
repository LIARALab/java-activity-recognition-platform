package org.liara.api.filter.interpretor;

import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.filter.parser.FilterParser;
import org.liara.api.filter.visitor.collection.EntityCollectionFilterVisitor;
import org.springframework.lang.NonNull;

public class BaseFilterInterpretor<Entity, Field> implements FilterInterpretor<Entity, Field>
{
  @NonNull
  private final FilterParser _parser;
  
  @NonNull
  private final EntityCollectionFilterVisitor<Entity, Field> _visitor;
  
  public BaseFilterInterpretor(
    @NonNull final FilterParser parser, 
    @NonNull final EntityCollectionFilterVisitor<Entity, Field> visitor
  ) {
    _parser = parser;
    _visitor = visitor;
  }

  @Override
  public FilterParser getParser () {
    return _parser;
  }

  @Override
  public EntityCollectionFilterVisitor<Entity, Field> getVisitor () {
    return _visitor;
  }

  @Override
  public Predicate execute (
    @NonNull final String filter, 
    @NonNull final EntityCollectionQuery<Entity, ?> query
  ) {
    _visitor.filter(query, _parser.parse(filter));
  }
}
