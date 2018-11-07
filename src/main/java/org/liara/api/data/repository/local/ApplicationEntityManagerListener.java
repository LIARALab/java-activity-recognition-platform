package org.liara.api.data.repository.local;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;

public interface ApplicationEntityManagerListener
{
  void onUpdate (
    @Nullable final ApplicationEntity oldEntity, @NonNull final ApplicationEntity newEntity
  );

  void onRemove (
    @NonNull final ApplicationEntity entity
  );

  @Nullable ApplicationEntityManager getParent ();

  void setParent (@Nullable final ApplicationEntityManager parent);
}
