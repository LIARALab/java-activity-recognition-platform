package org.liara.api.controller.model.relation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.WritableControllerConfiguration;
import org.liara.api.controller.collection.DeepChildrenNodeCollectionController;
import org.liara.api.data.entity.Node;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.rest.metamodel.collection.RestCollection;
import org.liara.rest.metamodel.relation.RestSubCollectionRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class DeepChildrenNodeRelation
  implements RestSubCollectionRelation<Node>
{
  @NonNull
  private final ApplicationContext _applicationContext;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public DeepChildrenNodeRelation (@NonNull final ApplicationContext applicationContext) {
    _applicationContext = applicationContext;
    _entityManager = applicationContext.getBean(EntityManager.class);
  }

  @Override
  public @NonNull RestCollection<Node> getRelatedCollection (
    @NonNull final Long identifier
  )
  {
    return _applicationContext.getBean(
      DeepChildrenNodeCollectionController.class,
      _applicationContext.getBean(WritableControllerConfiguration.class),
      _entityManager.find(Node.class, identifier)
    );
  }

  @Override
  public @NonNull Operator getRelatedOperator (@NonNull final Long identifier) {
    @NonNull final Node parent = _entityManager.find(Node.class, identifier);

    return Composition.of(
      Filter.expression(":this.coordinates.start > :super.coordinates.start"),
      Filter.expression(":this.coordinates.end < :super.coordinates.end")
    );
  }

  @Override
  public @NonNull Class<Node> getModelClass () {
    return Node.class;
  }
}
