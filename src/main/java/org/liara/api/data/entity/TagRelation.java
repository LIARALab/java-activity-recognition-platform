package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.relation.RelationFactory;
import org.liara.api.validation.ApplicationEntityReference;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.collection.operator.joining.Join;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag_relations")
public class TagRelation
  extends ApplicationEntity
{
  @RelationFactory(Tag.class)
  public static @NonNull Operator tags () {
    return ApplicationEntity.tags(TagRelation.class);
  }

  public static @NonNull Operator tags (@NonNull final TagRelation relation) {
    return ApplicationEntity.tags(TagRelation.class, relation);
  }

  @RelationFactory(TagRelation.class)
  public static @NonNull Operator tagRelations () {
    return ApplicationEntity.tagRelations(TagRelation.class);
  }

  public static @NonNull Operator tagRelations (@NonNull final TagRelation relation) {
    return ApplicationEntity.tagRelations(TagRelation.class, relation);
  }

  @RelationFactory(Tag.class)
  public static @NonNull Operator tag () {
    return Join.inner(Tag.class)
             .filter(Filter.expression(":this.identifier = :super.tagIdentifier"));
  }

  public static @NonNull Operator tag (@NonNull final TagRelation relation) {
    return Filter.expression(":this.identifier = :tagIdentifier")
             .setParameter("tagIdentifier", relation.getTagIdentifier());
  }

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
  @ApplicationEntityReference(Tag.class)
  public @Nullable Long getTagIdentifier () {
    return _tagIdentifier;
  }

  public void setTagIdentifier (@Nullable final Long tagIdentifier) {
    _tagIdentifier = tagIdentifier;
  }
}
