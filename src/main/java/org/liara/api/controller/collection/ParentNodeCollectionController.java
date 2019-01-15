package org.liara.api.controller.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.ReadableControllerConfiguration;
import org.liara.api.controller.WritableControllerConfiguration;
import org.liara.api.controller.model.ParentNodeModelController;
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
public class ParentNodeCollectionController
  extends BaseNodeCollectionController
{
  @NonNull
  private final ReadableControllerConfiguration _configuration;

  @NonNull
  private final Node _child;

  @Autowired
  public ParentNodeCollectionController (
    @NonNull final ReadableControllerConfiguration configuration, @NonNull final Node child
  )
  {
    super(configuration);
    _child = child;
    _configuration = configuration;
  }

  @Override
  public @NonNull JPAEntityCollection<Node> getCollection () {
    return super.getCollection().addFilter(Filter.expression(":this.coordinates.start < :childStart")
                                             .setParameter("childStart", _child.getCoordinates().getStart())).addFilter(
      Filter.expression(":this.coordinates.end > :childEnd")
        .setParameter("childEnd", _child.getCoordinates().getEnd()));
  }

  @Override
  public @NonNull RestModel<Node> getModelController () {
    return _configuration.getApplicationContext().getBean(
      ParentNodeModelController.class,
      _configuration.getApplicationContext().getBean(WritableControllerConfiguration.class),
      _child
    );
  }
}
