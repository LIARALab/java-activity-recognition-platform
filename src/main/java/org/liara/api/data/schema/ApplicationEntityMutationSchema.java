package org.liara.api.data.schema;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;

import java.util.UUID;

@Schema(ApplicationEntity.class)
public class ApplicationEntityMutationSchema
{
  @Nullable
  private UUID _uuid;

  public ApplicationEntityMutationSchema () {
    _uuid = null;
  }

  public ApplicationEntityMutationSchema (@NonNull final ApplicationEntityCreationSchema toCopy) {
    _uuid = toCopy.getUUID();
  }

  public @Nullable UUID getUUID () {
    return _uuid;
  }

  public void setUUID (@Nullable final UUID uuid) {
    _uuid = uuid;
  }
}
