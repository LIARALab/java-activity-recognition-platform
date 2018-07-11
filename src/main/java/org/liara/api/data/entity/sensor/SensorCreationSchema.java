package org.liara.api.data.entity.sensor;

import java.util.Optional;

import javax.validation.Valid;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.data.schema.Schema;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.liara.api.validation.Required;
import org.liara.api.validation.SensorType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
  private String _unit = null;
  
  @NonNull
  private ApplicationEntityReference<Node> _parent = ApplicationEntityReference.empty(Node.class);
  
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

  public String getUnit () {
    return _unit;
  }
  
  @JsonSetter
  public void setUnit (@Nullable final String unit) {
    _unit = unit;
  }
  
  public void setUnit (@NonNull final Optional<String> unit) {
    _unit = unit.orElse(null);
  }

  @Required
  @ValidApplicationEntityReference
  public ApplicationEntityReference<Node> getParent () {
    return _parent;
  }
  
  @JsonSetter
  public void setParent (@Nullable final Long identifier) {
    _parent = ApplicationEntityReference.of(Node.class, identifier);
  }
  
  public void setParent (@Nullable final Node parent) {
    _parent = (parent == null) ? ApplicationEntityReference.empty(Node.class)
                               : ApplicationEntityReference.of(parent);
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
