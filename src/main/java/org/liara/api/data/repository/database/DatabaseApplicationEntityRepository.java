package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.repository.ApplicationEntityRepository;
import org.liara.collection.operator.cursoring.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
@Primary
public class DatabaseApplicationEntityRepository<Entity extends ApplicationEntity>
       implements ApplicationEntityRepository<Entity>
{
  @NonNull
  private final EntityManager _entityManager;

  @NonNull
  private final Class<Entity> _type;
  
  @Autowired
  public DatabaseApplicationEntityRepository (
    @NonNull final EntityManager entityManager,
    @NonNull final Class<Entity> type
  ) {
    _entityManager = entityManager;
    _type = type;
  }
  
  @Override
  public @NonNull Optional<Entity> find (@Nullable final Long identifier) {
    return identifier == null ? Optional.empty() : Optional.ofNullable(_entityManager.find(_type, identifier));
  }

  @Override
  public @NonNull List<@NonNull Entity> findAll (@NonNull final Cursor cursor) {
    @NonNull final TypedQuery<Entity> query = _entityManager.createQuery(
      "SELECT entity FROM " + _type.getName() + " entity ORDER BY entity.identifier ASC", _type)
                                                            .setFirstResult(cursor.getOffset());

    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull Class<Entity> getManagedEntity () {
    return _type;
  }
}
