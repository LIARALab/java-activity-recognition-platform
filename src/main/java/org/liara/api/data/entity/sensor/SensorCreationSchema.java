package org.liara.api.data.entity.sensor;

import java.util.Optional;

import javax.validation.Valid;

import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.Schema;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.liara.api.validation.SensorType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Schema(Sensor.class)
@JsonDeserialize(using = SensorCreationSchemaDeserializer.class)
public class SensorCreationSchema
{
  @NonNull
  private String _name = null;
  
  @NonNull
  private String _type = null;
  
  @NonNull
  private String _valueUnit = null;
  
  @NonNull
  private String _valueLabel = null;
  
  @NonNull
  private String _ipv4Address = null;
  
  @NonNull
  private String _ipv6Address = null;
  
  @NonNull
  private Long _parent = null;
  
  @NonNull
  private SensorConfiguration _configuration = null;
  
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
  public Long getParent () {
    return _parent;
  }
  
  @JsonSetter
  public void setParent (@Nullable final Long parentIdentifier) {
    _parent = parentIdentifier;
  }
  
  public void setParent (@Nullable final Node parent) {
    if (parent == null) {
      _parent = null;
    } else {
      _parent = parent.getIdentifier();
    }
  }

  public void setParent (@NonNull final Optional<Long> parentIdentifier) {
    _parent = parentIdentifier.orElse(null);
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
