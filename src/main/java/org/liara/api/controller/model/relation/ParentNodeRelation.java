package org.liara.api.controller.model.relation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.model.NodeModelController;
import org.liara.api.data.entity.Node;
import org.liara.collection.operator.filtering.Filter;
import org.liara.collection.operator.joining.Join;
import org.liara.rest.metamodel.model.RestModel;
import org.liara.rest.metamodel.relation.RestSubModelRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ParentNodeRelation
  implements RestSubModelRelation<Node>
{
  @NonNull
  private final ApplicationContext _applicationContext;

  @NonNull
  private final EntityManager _entityManager;

  @Autowired
  public ParentNodeRelation (@NonNull final ApplicationContext applicationContext) {
    _applicationContext = applicationContext;
    _entityManager = applicationContext.getBean(EntityManager.class);
  }

  @Override
  public @NonNull RestModel<Node> getRestModel () {
    return _applicationContext.getBean(NodeModelController.class);
  }

  @Override
  public @NonNull Long getRelatedIdentifier (@NonNull final Long identifier)
  throws EntityNotFoundException
  {
    @NonNull final Node child = _entityManager.find(Node.class, identifier);

    @NonNull final List<@NonNull Node> result =
      _entityManager.createQuery("SELECT parent " + "  FROM " + Node.class.getName() + " parent " +
                                                                           " WHERE parent.coordinates.start < " +
                                 ":childStart" + "   AND parent.coordinates.end > :childEnd" +
                                                                           "   AND parent.coordinates.depth = " +
                                 ":childDepth - 1",
      Node.class
    ).setParameter(
      "childStart",
      child.getCoordinates().getStart()
    ).setParameter("childEnd", child.getCoordinates().getEnd()).setParameter(
      "childDepth",
      child.getCoordinates().getDepth()
    ).getResultList();

    if (result.isEmpty()) {
      throw new EntityNotFoundException("Unable to find parent of entity " + identifier);
    } else {
      return Objects.requireNonNull(result.get(0).getIdentifier());
    }
  }

  @Override
  public @NonNull Join<Node> getJoinOperator () {
    return Join.inner(Node.class, "parent").filter(Filter.expression(
      ":this.coordinates.start > :super.coordinates.start")).filter(Filter.expression(
      ":this.coordinates.end < :super.coordinates.end")).filter(Filter.expression(
      ":this.coordinates.depth = :super.coordinates.depth - 1"));
  }

  @Override
  public @NonNull Class<Node> getModelClass () {
    return Node.class;
  }
}
