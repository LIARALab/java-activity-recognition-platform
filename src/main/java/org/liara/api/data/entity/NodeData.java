package org.liara.api.data.entity;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class NodeData
{
  @NonNull
  private Optional<String> _name = Optional.empty();
  
  @NonNull
  private Optional<Long> _parent = Optional.empty();
 
  public Optional<String> getName () {
    return _name;
  }
  
  public void setName (@Nullable final String name) {
    if (name == null) {
      _name = Optional.empty();
    } else {
      _name = Optional.of(name);
    }
  }
  
  public void setName (@NonNull final Optional<String> name) {
    _name = name;
  }
  
  public Optional<Long> getParent () {
    return _parent;
  }
  
  public void setParent (final long parent) {
    _parent = Optional.of(parent);
  }
  
  public void setParent (@Nullable final Long parent) {
    if (parent == null) {
      _parent = Optional.empty();
    } else {
      _parent = Optional.of(parent);
    }
  }
  
  public void setParent (@Nullable final Node parent) {
    if (parent == null) {
      _parent = Optional.empty();
    } else {
      _parent = Optional.of(parent.getIdentifier());
    }
  }
  
  public void setParent (@NonNull final Optional<Long> parent) {
    _parent = parent;
  }
}
