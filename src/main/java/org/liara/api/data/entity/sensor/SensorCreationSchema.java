package org.liara.api.data.entity.sensor;

import java.util.Optional;

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

public class SensorCreationSchema
{
  @NonNull
  @Required
  private Optional<String> _name = Optional.empty();
  
  @NonNull
  @Required
  private Optional<String> _type = Optional.empty();
  
  @NonNull
  @Required
  private Optional<String> _valueType = Optional.empty();
  
  @NonNull
  private Optional<String> _valueUnit = Optional.empty();
  
  @NonNull
  private Optional<String> _valueLabel = Optional.empty();
  
  @NonNull
  private Optional<String> _ipv4Address = Optional.empty();
  
  @NonNull
  private Optional<String> _ipv6Address = Optional.empty();
  
  @NonNull
  @IdentifierOfEntityInCollection(collection = NodeCollection.class)
  private Optional<Long> _parentIdentifier = Optional.empty();
  
  @NonNull
  @Autowired
  private NodeCollection _nodes;
  
  @NonNull
  @Autowired
  private SensorCollection _sensors;
  
  @Nullable
  private Node _parent;

  public Optional<String> getName () {
    return _name;
  }
  
  @JsonSetter
  public void setName (@Nullable final String name) {
    _name = Optional.ofNullable(name);
  }

  public void setName (@NonNull final Optional<String> name) {
    _name = name;
  }

  public Optional<String> getType () {
    return _type;
  }
  
  @JsonSetter
  public void setType (@Nullable final String type) {
    _type = Optional.ofNullable(type);
  }

  public void setType (@NonNull final Optional<String> type) {
    _type = type;
  }

  public Optional<String> getValueType () {
    return _valueType;
  }

  @JsonSetter
  public void setValueType (@Nullable final String valueType) {
    _valueType = Optional.ofNullable(valueType);
  }

  public void setValueType (@NonNull final Optional<String> valueType) {
    _valueType = valueType;
  }

  public Optional<String> getValueUnit () {
    return _valueUnit;
  }

  @JsonSetter
  public void setValueUnit (@Nullable final String valueUnit) {
    _valueUnit = Optional.ofNullable(valueUnit);
  }
  
  public void setValueUnit (@NonNull final Optional<String> valueUnit) {
    _valueUnit = valueUnit;
  }

  public Optional<String> getValueLabel () {
    return _valueLabel;
  }
  
  @JsonSetter
  public void setValueLabel (@Nullable final String valueLabel) {
    _valueLabel = Optional.ofNullable(valueLabel);
  }

  public void setValueLabel (@NonNull final Optional<String> valueLabel) {
    _valueLabel = valueLabel;
  }

  public Optional<String> getIpv4Address () {
    return _ipv4Address;
  }
  
  @JsonSetter
  public void setIpv4Address (@Nullable final String ipv4Address) {
    _ipv4Address = Optional.ofNullable(ipv4Address);
  }

  public void setIpv4Address (@NonNull final Optional<String> ipv4Address) {
    _ipv4Address = ipv4Address;
  }

  public Optional<String> getIpv6Address () {
    return _ipv6Address;
  }
  
  @JsonSetter
  public void setIpv6Address (@Nullable final String ipv6Address) {
    _ipv6Address = Optional.ofNullable(ipv6Address);
  }

  public void setIpv6Address (@NonNull final Optional<String> ipv6Address) {
    _ipv6Address = ipv6Address;
  }

  public Optional<Long> getParentIdentifier () {
    return _parentIdentifier;
  }
  
  @JsonSetter
  public void setParentIdentifier (@Nullable final Long parentIdentifier) {
    _parentIdentifier = Optional.ofNullable(parentIdentifier);
    _parent = null;
  }
  
  public void setParentIdentifier (@Nullable final Node parent) {
    if (parent == null) {
      _parentIdentifier = Optional.empty();
    } else {
      _parentIdentifier = Optional.ofNullable(parent.getIdentifier());
    }
    _parent = null;
  }

  public void setParentIdentifier (@NonNull final Optional<Long> parentIdentifier) {
    _parentIdentifier = parentIdentifier;
    _parent = null;
  }
  
  public Node getParent () {
    if (!_parentIdentifier.isPresent()) {
      return null;
    }
    
    if (_parent == null) {
      try {
        _parent = _nodes.findByIdentifierOrFail(_parentIdentifier.get());
      } catch (final EntityNotFoundException exception) {
        throw new IllegalStateException(String.join(
          "",
          "Invalid SensorCreationSchema : the given parent node identifier does not",
          "exists in the application's node collection."
        ));
      }
    }
    
    return _parent;
  }
}
