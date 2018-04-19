package org.liara.api.collection.query.relation;

import java.util.Collection;

import javax.persistence.criteria.Path;

import org.liara.api.collection.query.EntityCollectionQuery;
import org.liara.api.collection.query.EntityCollectionSubquery;
import org.liara.api.collection.query.queried.QueriedEntity;
import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.springframework.lang.NonNull;

public class PathRelation<Base, Joined> implements EntityRelation<Base, Joined>
{
  @NonNull
  private final EntityFieldSelector<Base, Path<? extends Collection<Joined>>> _selector;

  public PathRelation(@NonNull final EntityFieldSelector<Base, Path<? extends Collection<Joined>>> selector) {
    _selector = selector;
  }

  @Override
  public void apply (
    @NonNull final EntityCollectionQuery<Base, ?> parent, 
    @NonNull final EntityCollectionSubquery<Joined, Joined> children
  ) {
    final QueriedEntity<?, Base> correlated = children.correlate(parent.getEntity());
    final Path<? extends Collection<Joined>> collection = _selector.select(children, correlated);
    
    children.andWhere(children.getEntity().in(collection));
  }
}
