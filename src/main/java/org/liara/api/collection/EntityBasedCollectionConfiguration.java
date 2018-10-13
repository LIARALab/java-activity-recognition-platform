package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.request.cursor.APIRequestFreeCursorParser;
import org.liara.api.request.cursor.APIRequestFreeCursorValidator;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestParser;
import org.liara.request.validator.APIRequestValidator;

import javax.persistence.EntityManager;

public class EntityBasedCollectionConfiguration<Entity>
  implements CollectionRequestConfiguration
{
  @NonNull
  private final Class<Entity> _entity;

  @NonNull
  private final EntityManager _entityManager;

  public EntityBasedCollectionConfiguration (
    @NonNull final EntityManager entityManager, @NonNull final Class<Entity> entity
  )
  {
    _entityManager = entityManager;
    _entity = entity;
  }

  @Override
  public @NonNull APIRequestValidator getValidator () {
    return APIRequestValidator.composse(
      EntityBasedOrderingConfiguration.getConfigurationOf(_entityManager, _entity).getValidator(),
      EntityBasedSelectionConfiguration.getConfigurationOf(_entityManager, _entity).getValidator(),
      new APIRequestFreeCursorValidator()
    );
  }

  @Override
  public @NonNull APIRequestParser<Operator> getParser () {
    return APIRequestParser.compose(
      EntityBasedOrderingConfiguration.getConfigurationOf(_entityManager, _entity).getParser(),
      EntityBasedSelectionConfiguration.getConfigurationOf(_entityManager, _entity).getParser(),
      new APIRequestFreeCursorParser()
    ).map(Composition::of);
  }
}
