package org.liara.api.data.entity.schema;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NodeSchema
{
  @Nullable
  private Long _identifier;

  @Nullable
  private String _name;

  @Nullable
  private Long _parent;

  public NodeSchema () {
    _name = null;
    _parent = null;
    _identifier = null;
  }

  public NodeSchema (@NonNull final NodeSchema toCopy) {
    _name = toCopy.getName();
    _parent = toCopy.getParent();
    _identifier = toCopy.getIdentifier();
  }

  public @Nullable Long getIdentifier () {
    return _identifier;
  }

  public void setIdentifier (@Nullable final Long identifier) {
    _identifier = identifier;
  }

  /**
   * Return the name of this node.
   * <p>
   * It's only a label in order to easily know what this node represents.
   *
   * @return The name of this node.
   */
  public @Nullable String getName () {
    return _name;
  }

  /**
   * Rename this node.
   *
   * @param name The new name to apply to this node.
   */
  public void setName (@Nullable final String name) {
    _name = name;
  }

  public @Nullable Long getParent () {
    return _parent;
  }

  public void setParent (@Nullable final Long parent) {
    _parent = parent;
  }
}
