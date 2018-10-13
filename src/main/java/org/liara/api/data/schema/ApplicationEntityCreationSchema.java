package org.liara.api.data.schema;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;

import java.util.UUID;

@Schema(ApplicationEntity.class)
public class ApplicationEntityCreationSchema
{
  @Nullable
  private UUID _uuid;

  public ApplicationEntityCreationSchema () {
    _uuid = null;
  }

  public ApplicationEntityCreationSchema (@NonNull final ApplicationEntityCreationSchema toCopy) {
    _uuid = toCopy.getUUID();
  }

  public @Nullable UUID getUUID () {
    return _uuid;
  }

  public void setUUID (@Nullable final UUID uuid) {
    _uuid = uuid;
  }
}
