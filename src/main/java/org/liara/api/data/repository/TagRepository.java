package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Tag;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import java.util.List;

public interface TagRepository
  extends ApplicationEntityRepository<Tag>
{
  @NonNull List<@NonNull Tag> findTagsOf (
    @NonNull final Class<? extends ApplicationEntity> type, @Nullable final Long identifier
  );

  default @NonNull List<@NonNull Tag> findTagsOf (@NonNull final ApplicationEntityReference<?> reference) {
    return findTagsOf(reference.getType(), reference.getIdentifier());
  }
}
