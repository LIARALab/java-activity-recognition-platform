package org.liara.api.filter.interpretor;

import javax.persistence.criteria.Expression;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.filter.parser.TextFilterParser;
import org.liara.api.filter.visitor.collection.EntityCollectionTextFilterVisitor;
import org.springframework.lang.NonNull;

public class TextFilterInterpretor<Entity> extends BaseFilterInterpretor<Entity, String>
{
  public TextFilterInterpretor (
    @NonNull final EntityFieldSelector<Entity, Expression<String>> field
  ) {
    super(
      new TextFilterParser(),
      new EntityCollectionTextFilterVisitor<>(field)
    );
  }
}
