package org.liara.api.data.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.Tag;

import java.util.List;

public interface TagRepository
  extends ApplicationEntityRepository<Tag>
{
  @NonNull List<@NonNull Tag> findTagsOf (
    @NonNull final Class<? extends ApplicationEntity> type, @NonNull final Long identifier
  );

  default @NonNull List<@NonNull Tag> findTagsOf (
    @NonNull final ApplicationEntity entity
  )
  {
    return findTagsOf(
      entity.getClass(),
      entity.getIdentifier()
    );
  }
}
