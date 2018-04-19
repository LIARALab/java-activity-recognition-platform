package org.liara.api.filter.interpretor;

import javax.persistence.criteria.Predicate;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.filter.parser.FilterParser;
import org.liara.api.filter.visitor.collection.EntityCollectionFilterVisitor;
import org.springframework.lang.NonNull;

public interface FilterInterpretor<Entity, Field>
{
  public FilterParser getParser ();
  
  public EntityCollectionFilterVisitor<Entity, Field> getVisitor ();
  
  public Predicate execute (
    @NonNull final String filter, 
    @NonNull final EntityCollectionQuery<Entity, ?> query
  );
}
