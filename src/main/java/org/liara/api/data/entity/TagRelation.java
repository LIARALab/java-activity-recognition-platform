package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag_relations")
public class TagRelation
  extends ApplicationEntity
{
  @Nullable
  private Long _entityIdentifier;

  @Nullable
  private Class<? extends ApplicationEntity> _entityType;

  @Nullable
  private Long _tagIdentifier;

  public TagRelation () {
    _entityIdentifier = null;
    _entityType = null;
    _tagIdentifier = null;
  }

  public TagRelation (@NonNull final TagRelation toCopy) {
    _entityIdentifier = toCopy.getEntityIdentifier();
    _entityType = toCopy.getEntityType();
    _tagIdentifier = toCopy.getTagIdentifier();
  }

  @Column(name = "entity_identifier", nullable = false)
  public @Nullable Long getEntityIdentifier () {
    return _entityIdentifier;
  }

  public void setEntityIdentifier (
    @Nullable final Long entityIdentifier
  )
  {
    _entityIdentifier = entityIdentifier;
  }

  @Column(name = "entity_type", nullable = false)
  public @Nullable Class<? extends ApplicationEntity> getEntityType () {
    return _entityType;
  }

  public void setEntityType (@Nullable final Class<? extends ApplicationEntity> entityType) {
    _entityType = entityType;
  }

  @Column(name = "tag_identifier", nullable = false)
  public @Nullable Long getTagIdentifier () {
    return _tagIdentifier;
  }

  public void setTagIdentifier (@Nullable final Long tagIdentifier) {
    _tagIdentifier = tagIdentifier;
  }
}
