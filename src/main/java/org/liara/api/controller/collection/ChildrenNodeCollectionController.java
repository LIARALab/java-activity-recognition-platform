package org.liara.api.controller.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.ReadableControllerConfiguration;
import org.liara.api.controller.WritableControllerConfiguration;
import org.liara.api.controller.model.ChildNodeModelController;
import org.liara.api.data.entity.Node;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.collection.operator.filtering.Filter;
import org.liara.rest.metamodel.model.RestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChildrenNodeCollectionController
  extends BaseNodeCollectionController
{
  @NonNull
  private final ReadableControllerConfiguration _configuration;

  @NonNull
  private final Node _parent;

  @Autowired
  public ChildrenNodeCollectionController (
    @NonNull final ReadableControllerConfiguration configuration, @NonNull final Node parent
  )
  {
    super(configuration);
    _parent = parent;
    _configuration = configuration;
  }

  @Override
  public @NonNull JPAEntityCollection<Node> getCollection () {
    return super.getCollection().addFilter(
      Filter.expression(":this.coordinates.start > :parentStart")
        .setParameter("parentStart", _parent.getCoordinates().getStart())).addFilter(
      Filter.expression(":this.coordinates.end < :parentEnd")
        .setParameter("parentEnd", _parent.getCoordinates().getEnd())).addFilter(Filter.expression(
      ":this.coordinates.depth = :parentDepth + 1").setParameter("parentDepth", _parent.getCoordinates().getDepth()));
  }

  @Override
  public @NonNull RestModel<Node> getModelController () {
    return _configuration.getApplicationContext().getBean(
      ChildNodeModelController.class,
      _configuration.getApplicationContext().getBean(WritableControllerConfiguration.class),
      _parent
    );
  }
}
