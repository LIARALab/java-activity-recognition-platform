package org.liara.api.data.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.Column;
import javax.persistence.Transient;

public class NodeSchema
  extends ApplicationEntity
{
  @Nullable
  private String _name;

  @Nullable
  private ApplicationEntityReference<Node> _parent;

  public NodeSchema () {
    _name = null;
    _parent = ApplicationEntityReference.empty(Node.class);
  }

  public NodeSchema (@NonNull final NodeSchema toCopy) {
    super(toCopy);
    _name = toCopy.getName();
    _parent = toCopy.getParent();
  }

  /**
   * Return the name of this node.
   * <p>
   * It's only a label in order to easily know what this node represents.
   *
   * @return The name of this node.
   */
  @Column(name = "name", nullable = false)
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

  public @Nullable
  final ApplicationEntityReference<Node> getParent () {
    return _parent;
  }

  public void setParent (@Nullable final ApplicationEntityReference<Node> parent) {
    _parent = parent;
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends NodeSchema> getReference () {
    return ApplicationEntityReference.of(this);
  }
}
