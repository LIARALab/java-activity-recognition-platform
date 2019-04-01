package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.CacheMode;
import org.hibernate.jpa.QueryHints;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.repository.ApplicationEntityRepository;
import org.liara.api.io.WritingSession;
import org.liara.collection.operator.cursoring.Cursor;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class DatabaseApplicationEntityRepository<Entity extends ApplicationEntity>
       implements ApplicationEntityRepository<Entity>
{
  @NonNull
  private final WritingSession _writingSession;

  @NonNull
  private final Class<Entity> _type;

  public DatabaseApplicationEntityRepository (
    @NonNull final WritingSession writingSession,
    @NonNull final Class<Entity> type
  ) {
    _writingSession = writingSession;
    _type = type;
  }
  
  @Override
  public @NonNull Optional<Entity> find (@Nullable final Long identifier) {
    return Optional.ofNullable(identifier).map(this::doFind);
  }

  private @Nullable Entity doFind (@NonNull final Long identifier) {
    return _writingSession.getEntityManager().find(_type, identifier);
  }

  @Override
  public @NonNull List<@NonNull Entity> findAll (@NonNull final Cursor cursor) {
    @NonNull final TypedQuery<Entity> query = _writingSession.getEntityManager().createQuery(
      "SELECT entity FROM " + _type.getName() + " entity ORDER BY entity.identifier ASC", _type
    );

    query.setFirstResult(cursor.getOffset());
    query.setHint(QueryHints.HINT_CACHE_MODE, CacheMode.IGNORE);
    if (cursor.hasLimit()) query.setMaxResults(cursor.getLimit());

    return query.getResultList();
  }

  @Override
  public @NonNull Class<Entity> getManagedEntity () {
    return _type;
  }
}
