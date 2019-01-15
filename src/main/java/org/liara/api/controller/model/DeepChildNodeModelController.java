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
public class DeepChildNodeModelController
  extends BaseNodeModelController
{
  @NonNull
  private final WritableControllerConfiguration _configuration;

  @NonNull
  private final Node _parent;

  @Autowired
  public DeepChildNodeModelController (
    @NonNull final WritableControllerConfiguration configuration, @NonNull final Node parent
  )
  {
    super(configuration);
    _parent = parent;
    _configuration = configuration;
  }

  @Override
  public @NonNull Node get (@NonNull final Long identifier)
  throws EntityNotFoundException
  {
    @NonNull final List<@NonNull Node> result = getCollection().addFilter(Filter.expression(
      ":this.identifier = :identifier").setParameter("identifier", identifier)).find();

    if (result.isEmpty()) throw new EntityNotFoundException(
      "Unable to find deep child " + identifier + " of " + _parent.getIdentifier());

    return result.get(0);
  }

  @Override
  public @NonNull Node get (@NonNull final UUID identifier)
  throws EntityNotFoundException
  {
    @NonNull final List<@NonNull Node> result = getCollection().addFilter(Filter.expression(
      ":this.universalUniqueIdentifier = :identifier").setParameter("identifier", identifier)).find();

    if (result.isEmpty()) throw new EntityNotFoundException(
      "Unable to find deep child " + identifier.toString() + " of " + _parent.getIdentifier());

    return result.get(0);
  }

  private @NonNull JPAEntityCollection<Node> getCollection () {
    return new JPAEntityCollection<>(_configuration.getEntityManager(), getModelClass()).addFilter(Filter.expression(
      ":this.coordinates.start > :parentStart").setParameter("parentStart", _parent.getCoordinates().getStart()))
             .addFilter(Filter.expression(":this.coordinates.end < :parentEnd")
                          .setParameter("parentEnd", _parent.getCoordinates().getEnd()));
  }
}
