package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag
  extends ApplicationEntity
{
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

  @Override
  public @NonNull Tag clone () {
    return new Tag();
  }
}
