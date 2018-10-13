package org.liara.api.data.schema;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.Sensor;
import org.liara.api.data.entity.SensorConfiguration;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.validation.Required;
import org.liara.api.validation.SensorType;
import org.liara.api.validation.ValidApplicationEntityReference;

import javax.validation.Valid;

@Schema(Sensor.class)
@JsonDeserialize(using = SensorCreationSchemaDeserializer.class)
public class SensorCreationSchema
  extends ApplicationEntityCreationSchema
{
  @Nullable
  private String _name = null;

  @Nullable
  private String _type = null;

  @Nullable
  private String _unit = null;

  @Nullable
  private ApplicationEntityReference<Node> _parent = null;

  @Nullable
  private SensorConfiguration _configuration = null;

  @Required
  public @Nullable String getName () {
    return _name;
  }

  public void setName (@Nullable final String name) {
    _name = name;
  }

  @Required
  @SensorType
  public @Nullable String getType () {
    return _type;
  }

  @JsonSetter
  public void setType (@Nullable final String type) {
    _type = type;
  }

  public @Nullable String getUnit () {
    return _unit;
  }

  @JsonSetter
  public void setUnit (@Nullable final String unit) {
    _unit = unit;
  }

  @Required
  @ValidApplicationEntityReference
  public @Nullable ApplicationEntityReference<Node> getParent () {
    return _parent;
  }

  @JsonSetter
  public void setParent (@Nullable final ApplicationEntityReference<Node> parent) {
    _parent = parent;
  }

  @Valid
  public @Nullable SensorConfiguration getConfiguration () {
    return _configuration;
  }

  @JsonSetter
  public void setConfiguration (@Nullable final SensorConfiguration configuration) {
    _configuration = configuration;
  }
}
