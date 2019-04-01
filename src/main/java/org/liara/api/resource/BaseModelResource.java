package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.relation.Relation;
import org.liara.api.relation.RelationManager;
import org.liara.collection.ModelCollection;
import org.liara.collection.jpa.JPACollections;
import org.liara.rest.metamodel.RestResource;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class BaseModelResource<Model extends ApplicationEntity>
  extends ModelResource<Model>
{
  @NonNull
  private final RelationManager _relationManager;

  @NonNull
  private final CollectionResourceBuilder _collectionResourceBuilder;

  public BaseModelResource (@NonNull final BaseModelResourceBuilder<Model> builder) {
    super(
      Objects.requireNonNull(builder.getModelClass()),
      Objects.requireNonNull(builder.getModel())
    );

    _relationManager = Objects.requireNonNull(builder.getRelationManager());
    _collectionResourceBuilder = Objects.requireNonNull(builder.getCollectionResourceBuilder());
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
      @NonNull final List<?> results = fetchFirst(relation);

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

  private @NonNull List<@NonNull ?> fetchFirst (@NonNull final Relation relation) {
    final org.liara.collection.@NonNull Collection<?> collection = relation.getOperator(
      getModel()
    ).apply(ModelCollection.create(relation.getDestinationClass()));

    @NonNull final EntityManager entityManager =
      _collectionResourceBuilder.getEntityManagerFactory().createEntityManager();
    entityManager.getTransaction().begin();

    @NonNull final TypedQuery<?> query = entityManager.createQuery(
      JPACollections.getQuery(collection, ":this").toString(),
      collection.getModelClass()
    ).setFirstResult(0).setMaxResults(1);

    JPACollections.getParameters(collection).forEach(query::setParameter);

    @NonNull final List<@NonNull ?> result = query.getResultList();

    entityManager.getTransaction().commit();
    entityManager.close();

    return result;
  }
}
