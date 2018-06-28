package org.liara.api.collection;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.metamodel.SingularAttribute;

import org.liara.api.collection.query.selector.EntityFieldSelector;
import org.liara.api.collection.query.selector.SimpleEntityFieldSelector;
import org.liara.api.collection.transformation.operator.EntityCollectionOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionExpressionOrderingOperator;
import org.liara.api.collection.transformation.operator.ordering.EntityCollectionOrderingOperator;
import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.lang.NonNull;

public final class Operators
{
  public static <Entity extends ApplicationEntity> EntityCollectionOperator<Entity> except (@NonNull final Entity entity) {
    return (query) -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(
        builder.not(
          builder.equal(
            query.getEntity().get("_identifier"),
            entity.getIdentifier()
          )
        )
      );
    };
  }
  
  public static <Entity> EntityCollectionOperator<Entity> orderAscendingBy (
    @NonNull final String field
  ) {
    return Operators.orderAscendingBy(entity -> entity.get(field));
  }
  
  public static <Entity, Field> EntityCollectionOperator<Entity> orderAscendingBy (
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Field>> field
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(
        field, 
        EntityCollectionOrderingOperator.Direction.ASC
    );
  }

  public static <Entity, Field> EntityCollectionOperator<Entity> orderAscendingBy (
    @NonNull final EntityFieldSelector<Entity, Expression<Field>> field
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(
        field, 
        EntityCollectionOrderingOperator.Direction.ASC
    );
  }
  
  public static <Entity> EntityCollectionOperator<Entity> orderDescendingBy (
    @NonNull final String field
  ) {
    return Operators.orderDescendingBy(entity -> entity.get(field));
  }
  
  public static <Entity, Field> EntityCollectionOperator<Entity> orderDescendingBy (
    @NonNull final SimpleEntityFieldSelector<Entity, Expression<Field>> field
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(
        field, 
        EntityCollectionOrderingOperator.Direction.DESC
    );
  }

  public static <Entity, Field> EntityCollectionOperator<Entity> orderDescendingBy (
    @NonNull final EntityFieldSelector<Entity, Expression<Field>> field
  ) {
    return new EntityCollectionExpressionOrderingOperator<>(
        field, 
        EntityCollectionOrderingOperator.Direction.DESC
    );
  } 
  
  public static <Entity> EntityCollectionOperator<Entity> notNull (
    @NonNull final String field
  ) {
    return query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(builder.isNotNull(query.getEntity().get(field)));
    };
  }
  
  public static <Entity, Field> EntityCollectionOperator<Entity> notNull (
    @NonNull final SingularAttribute<? super Entity, Field> field
  ) {
    return query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(builder.isNotNull(query.getEntity().get(field)));
    };
  }

  public static <Entity, Value> EntityCollectionOperator<Entity> equal (
    @NonNull final String field, 
    @NonNull final Value value
  ) {
    return query -> {
      final CriteriaBuilder builder = query.getManager().getCriteriaBuilder();
      query.andWhere(builder.equal(query.getEntity().get(field), value));
    };
  }

  public static <Entity> EntityCollectionOperator<Entity> in (
    @NonNull final List<Entity> allowed
  ) {
    return query -> {
      query.andWhere(query.getEntity().in(allowed));
    };
  }
}
