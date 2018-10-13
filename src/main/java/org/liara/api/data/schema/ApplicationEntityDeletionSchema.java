package org.liara.api.data.schema;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.validation.Required;

@Schema(ApplicationEntity.class)
public class ApplicationEntityDeletionSchema
  implements Cloneable
{
  @Nullable
  @Required
  private ApplicationEntityReference _reference;

  public ApplicationEntityDeletionSchema () {
    _reference = null;
  }

  public ApplicationEntityDeletionSchema (@NonNull final ApplicationEntity toDelete) {
    _reference = toDelete.getReference();
  }

  public ApplicationEntityDeletionSchema (@NonNull final ApplicationEntityReference reference) {
    _reference = reference;
  }

  public ApplicationEntityDeletionSchema (@NonNull final ApplicationEntityDeletionSchema toCopy) {
    _reference = toCopy.getReference();
  }

  public @Nullable ApplicationEntityReference getReference () {
    return _reference;
  }

  public void setReference (@Nullable final ApplicationEntityReference reference) {
    _reference = reference;
  }
}
