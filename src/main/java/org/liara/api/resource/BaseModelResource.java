package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.relation.Relation;
import org.liara.api.relation.RelationManager;
import org.liara.api.utils.Builder;
import org.liara.collection.Collection;
import org.liara.collection.jpa.JPAEntityCollection;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.cursoring.Cursor;
import org.liara.rest.metamodel.RestResource;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class BaseModelResource<Model extends ApplicationEntity>
  extends ModelResource<Model>
{
  @NonNull
  private final RelationManager _relationManager;

  @NonNull
  private final CollectionResourceBuilder _collectionResourceBuilder;

  public BaseModelResource (@NonNull final BaseModelResourceBuilder<Model> builder) {
    super(
      Builder.require(builder.getModelClass()),
      Builder.require(builder.getModel())
    );

    _relationManager = Builder.require(builder.getRelationManager());
    _collectionResourceBuilder = Builder.require(builder.getCollectionResourceBuilder());
  }

  @Override
  public boolean hasResource (@NonNull final String name) {
    return _relationManager.getRelationsOf(getModelClass()).containsKey(name.toLowerCase());
  }

  @Override
  public @NonNull RestResource getResource (@NonNull final String name)
  throws NoSuchElementException {
    @NonNull final String loweredName = name.toLowerCase();
    @NonNull
    final Map<@NonNull String, @NonNull Relation> relations = _relationManager.getRelationsOf(
      getModelClass()
    );

    if (relations.containsKey(name)) {
      return getRelationResource(relations.get(name));
    }

    return super.getResource(name);
  }

  private @NonNull RestResource getRelationResource (
    @NonNull final Relation relation
  )
  throws NoSuchElementException {
    if (relation.isCollectionRelation()) {
      return new StaticComputedCollectionResource(
        relation.getDestinationClass(),
        relation.getOperator(getModel()),
        _collectionResourceBuilder
      );
    } else {
      @NonNull final Collection collection = new JPAEntityCollection(
        _collectionResourceBuilder.getEntityManager(),
        relation.getDestinationClass()
      );

      @NonNull final List<Object> results = Composition.of(
        Cursor.FIRST,
        relation.getOperator(getModel())
      ).apply(collection).fetch();

      if (results.isEmpty()) {
        throw new NoSuchElementException();
      } else {
        BaseModelResourceBuilder builder = new BaseModelResourceBuilder();
        builder.setModelClass(relation.getDestinationClass());
        builder.setModel((ApplicationEntity) results.get(0));
        builder.setCollectionResourceBuilder(_collectionResourceBuilder);
        builder.setRelationManager(_relationManager);
        return builder.build();
      }
    }
  }
}
