package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.repository.ApplicationEntityRepository;
import org.liara.collection.operator.cursoring.Cursor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LocalApplicationEntityRepository<Entity extends ApplicationEntity>
  extends LocalRepository
       implements ApplicationEntityRepository<Entity>
{
  @NonNull
  private final Class<Entity> _type;

  public LocalApplicationEntityRepository (@NonNull final Class<Entity> type) {
    super();
    _type = type;
  }

  public LocalApplicationEntityRepository (@NonNull final LocalApplicationEntityRepository<Entity> toCopy) {
    super(toCopy);
    _type = toCopy.getManagedEntity();
  }

  @Override
  public @NonNull Optional<Entity> find (@Nullable final Long identifier) {
    return identifier == null || getParent() == null ? Optional.empty()
                                                     : Optional.of(getParent().get(_type, identifier));
  }

  @Override
  public @NonNull List<@NonNull Entity> findAll (@NonNull final Cursor cursor) {
    if (getParent() == null) return Collections.emptyList();

    @NonNull final List<@NonNull Entity> result = getParent().findAll(_type);

    if (cursor.hasLimit()) {
      return result.subList(cursor.getOffset(), cursor.getOffset() + cursor.getLimit());
    } else {
      return result.subList(cursor.getOffset(), result.size());
    }
  }

  @Override
  public void onUpdate (
    @Nullable final ApplicationEntity oldEntity, @NonNull final ApplicationEntity newEntity
  )
  { }

  @Override
  public void onRemove (
    @NonNull final ApplicationEntity entity
  )
  { }

  @Override
  public @NonNull Class<Entity> getManagedEntity () {
    return _type;
  }
}
