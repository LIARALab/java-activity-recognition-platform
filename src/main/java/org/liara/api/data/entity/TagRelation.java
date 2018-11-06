package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag_relations")
public class TagRelation
  extends ApplicationEntity
{
  @Nullable
  private ApplicationEntityReference<? extends ApplicationEntity> _entityIdentifier;

  @Nullable
  private Class<? extends ApplicationEntity> _entityType;

  @Nullable
  private ApplicationEntityReference<? extends Tag> _tagIdentifier;

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
  public @Nullable ApplicationEntityReference<? extends ApplicationEntity> getEntityIdentifier () {
    return _entityIdentifier;
  }

  public void setEntityIdentifier (
    @Nullable final ApplicationEntityReference<? extends ApplicationEntity> entityIdentifier
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
  public @Nullable ApplicationEntityReference<? extends Tag> getTagIdentifier () {
    return _tagIdentifier;
  }

  public void setTagIdentifier (@Nullable final ApplicationEntityReference<? extends Tag> tagIdentifier) {
    _tagIdentifier = tagIdentifier;
  }

  @Override
  public @NonNull TagRelation clone () {
    return new TagRelation(this);
  }
}
