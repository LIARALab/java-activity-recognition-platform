package org.liara.api.controller.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.controller.WritableControllerConfiguration;
import org.liara.api.data.entity.Node;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.collection.operator.filtering.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ParentNodeModelController
  extends BaseNodeModelController
{
  @NonNull
  private final WritableControllerConfiguration _configuration;

  @NonNull
  private final Node _child;

  @Autowired
  public ParentNodeModelController (
    @NonNull final WritableControllerConfiguration configuration, @NonNull final Node child
  )
  {
    super(configuration);
    _child = child;
    _configuration = configuration;
  }

  @Override
  public @NonNull Node get (@NonNull final Long identifier)
  throws EntityNotFoundException
  {
    @NonNull final List<@NonNull Node> result = getCollection().addFilter(Filter.expression(
      ":this.identifier = :identifier").setParameter("identifier", identifier)).find();

    if (result.isEmpty()) throw new EntityNotFoundException(
      "Unable to find parent " + identifier + " of " + _child.getIdentifier());

    return result.get(0);
  }

  @Override
  public @NonNull Node get (@NonNull final UUID identifier)
  throws EntityNotFoundException
  {
    @NonNull final List<@NonNull Node> result = getCollection().addFilter(Filter.expression(
      ":this.universalUniqueIdentifier = :identifier").setParameter("identifier", identifier)).find();

    if (result.isEmpty()) throw new EntityNotFoundException(
      "Unable to find parent " + identifier.toString() + " of " + _child.getIdentifier());

    return result.get(0);
  }

  private @NonNull JPAEntityCollection<Node> getCollection () {
    return new JPAEntityCollection<>(_configuration.getEntityManager(), getModelClass()).addFilter(Filter.expression(
      ":this.coordinates.start < :childStart").setParameter("childStart", _child.getCoordinates().getStart()))
             .addFilter(Filter.expression(":this.coordinates.end > :childEnd")
                          .setParameter("childEnd", _child.getCoordinates().getEnd()));
  }
}
