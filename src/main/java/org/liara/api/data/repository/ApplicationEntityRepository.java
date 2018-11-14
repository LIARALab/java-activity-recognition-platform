package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.collection.operator.cursoring.Cursor;

import java.util.List;
import java.util.Optional;

public interface ApplicationEntityRepository<Entity extends ApplicationEntity>
{
  @NonNull Optional<Entity> find (@Nullable final Long identifier);

  default Entity getAt (@NonNull final Long identifier) {
    return find(identifier).orElseThrow();
  }

  default @NonNull List<@NonNull Entity> findAll () {
    return findAll(Cursor.ALL);
  }

  @NonNull List<@NonNull Entity> findAll (@NonNull final Cursor cursor);

  @NonNull Class<Entity> getManagedEntity ();
}
