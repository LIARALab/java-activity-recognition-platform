package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.relation.Relation;
import org.liara.api.relation.RelationManager;
import org.liara.collection.ModelCollection;
import org.liara.collection.jpa.JPACollections;
import org.liara.rest.metamodel.RestResource;
import org.springframework.context.ApplicationContext;

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
  private final ApplicationContext _context;

  public BaseModelResource (@NonNull final BaseModelResourceBuilder<Model> builder) {
    super(
      Objects.requireNonNull(builder.getModelClass()),
      Objects.requireNonNull(builder.getModel())
    );

    _relationManager = Objects.requireNonNull(builder.getRelationManager());
    _context = Objects.requireNonNull(builder.getContext());
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
        _context.getBean(CollectionResourceBuilder.class)
      );
    } else {
      @NonNull final List<?> results = fetchFirst(relation);

      if (results.isEmpty()) {
        throw new NoSuchElementException();
      } else {
        @NonNull final BaseModelResourceBuilder builder = (
          _context.getBean(BaseModelResourceBuilder.class)
        );
        builder.setModelClass(relation.getDestinationClass());
        builder.setModel((ApplicationEntity) results.get(0));
        return builder.build();
      }
    }
  }

  private @NonNull List<@NonNull ?> fetchFirst (@NonNull final Relation relation) {
    final org.liara.collection.@NonNull Collection<?> collection = relation.getOperator(
      getModel()
    ).apply(ModelCollection.create(relation.getDestinationClass()));

    @NonNull final EntityManager entityManager = _context.getBean(EntityManager.class);
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
