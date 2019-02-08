package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.relation.RelationFactory;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag
  extends ApplicationEntity
{
  @RelationFactory(Tag.class)
  public static @NonNull Operator tags () {
    return ApplicationEntity.tags(Tag.class);
  }

  public static @NonNull Operator tags (@NonNull final Tag tag) {
    return ApplicationEntity.tags(Tag.class, tag);
  }

  @RelationFactory(TagRelation.class)
  public static @NonNull Operator tagRelations () {
    return ApplicationEntity.tagRelations(Tag.class);
  }

  public static @NonNull Operator tagRelations (@NonNull final Tag tag) {
    return ApplicationEntity.tagRelations(Tag.class, tag);
  }

  @RelationFactory(TagRelation.class)
  public static @NonNull Operator relations () {
    return Filter.expression(":this.tagIdentifier = :super.identifier");
  }

  public static @NonNull Operator relations (@NonNull final Tag tag) {
    return Filter.expression(":this.tagIdentifier = :tagIdentifier")
             .setParameter("tagIdentifier", tag.getIdentifier());
  }

  @Nullable
  private String _name;

  public Tag () {
    _name = null;
  }

  public Tag (@NonNull final Tag toCopy) {
    super(toCopy);
    _name = toCopy.getName();
  }

  @Column(name = "name", unique = true, nullable = false)
  public @Nullable String getName () {
    return _name;
  }

  public void setName (@Nullable final String name) {
    _name = name;
  }
}
