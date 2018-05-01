package org.liara.api.data.entity.sensor;

import java.util.Optional;

import javax.validation.Valid;

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.collection.SensorCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.liara.api.validation.SensorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Component
@Scope("prototype")
@JsonDeserialize(using = SensorCreationSchemaDeserializer.class)
public class SensorCreationSchema
{
  @NonNull
  private String _name = null;
  
  @NonNull
  private String _type = null;
  
  @NonNull
  private String _valueType = null;
  
  @NonNull
  private String _valueUnit = null;
  
  @NonNull
  private String _valueLabel = null;
  
  @NonNull
  private String _ipv4Address = null;
  
  @NonNull
  private String _ipv6Address = null;
  
  @NonNull
  private Long _parentIdentifier = null;
  
  @NonNull
  private SensorConfiguration _configuration = null;
  
  @NonNull
  @Autowired
  private NodeCollection _nodes;
  
  @NonNull
  @Autowired
  private SensorCollection _sensors;
  
  @Nullable
  private Node _parent = null;

  @Required
  public String getName () {
    return _name;
  }
  
  @JsonSetter
  public void setName (@Nullable final String name) {
    _name = name;
  }

  public void setName (@NonNull final Optional<String> name) {
    _name = name.orElse(null);
  }

  @Required
  @SensorType
  public String getType () {
    return _type;
  }
  
  @JsonSetter
  public void setType (@Nullable final String type) {
    _type = type;
  }

  public void setType (@NonNull final Optional<String> type) {
    _type = type.orElse(null);
  }

  @Required
  public String getValueType () {
    return _valueType;
  }

  @JsonSetter
  public void setValueType (@Nullable final String valueType) {
    _valueType = valueType;
  }

  public void setValueType (@NonNull final Optional<String> valueType) {
    _valueType = valueType.orElse(null);
  }

  public String getValueUnit () {
    return _valueUnit;
  }
  
  @JsonIgnore
  public Optional<String> getOptionalValueUnit () {
    return Optional.ofNullable(_valueUnit);
  }

  @JsonSetter
  public void setValueUnit (@Nullable final String valueUnit) {
    _valueUnit = valueUnit;
  }
  
  public void setValueUnit (@NonNull final Optional<String> valueUnit) {
    _valueUnit = valueUnit.orElse(null);
  }

  public String getValueLabel () {
    return _valueLabel;
  }

  @JsonIgnore
  public Optional<String> getOptionalValueLabel () {
    return Optional.ofNullable(_valueLabel);
  }
  
  @JsonSetter
  public void setValueLabel (@Nullable final String valueLabel) {
    _valueLabel = valueLabel;
  }

  public void setValueLabel (@NonNull final Optional<String> valueLabel) {
    _valueLabel = valueLabel.orElse(null);
  }

  public String getIpv4Address () {
    return _ipv4Address;
  }
  
  @JsonSetter
  public void setIpv4Address (@Nullable final String ipv4Address) {
    _ipv4Address = ipv4Address;
  }

  public void setIpv4Address (@NonNull final Optional<String> ipv4Address) {
    _ipv4Address = ipv4Address.orElse(null);
  }

  public String getIpv6Address () {
    return _ipv6Address;
  }
  
  @JsonSetter
  public void setIpv6Address (@Nullable final String ipv6Address) {
    _ipv6Address = ipv6Address;
  }

  public void setIpv6Address (@NonNull final Optional<String> ipv6Address) {
    _ipv6Address = ipv6Address.orElse(null);
  }

  @IdentifierOfEntityInCollection(collection = NodeCollection.class)
  public Long getParentIdentifier () {
    return _parentIdentifier;
  }
  
  @JsonSetter
  public void setParentIdentifier (@Nullable final Long parentIdentifier) {
    _parentIdentifier = parentIdentifier;
    _parent = null;
  }
  
  public void setParentIdentifier (@Nullable final Node parent) {
    if (parent == null) {
      _parentIdentifier = null;
    } else {
      _parentIdentifier = parent.getIdentifier();
    }
    _parent = null;
  }

  public void setParentIdentifier (@NonNull final Optional<Long> parentIdentifier) {
    _parentIdentifier = parentIdentifier.orElse(null);
    _parent = null;
  }
  
  public Node getParent () {
    if (_parentIdentifier == null) {
      return null;
    }
    
    if (_parent == null) {
      try {
        _parent = _nodes.findByIdentifierOrFail(_parentIdentifier);
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

  @Valid
  public SensorConfiguration getConfiguration () {
    return _configuration;
  }
  
  @JsonSetter
  public void setConfiguration (@Nullable final SensorConfiguration configuration) {
    _configuration = configuration;
  }

  public void setConfiguration (@NonNull final Optional<SensorConfiguration> configuration) {
    _configuration = configuration.orElse(null);
  }
}
