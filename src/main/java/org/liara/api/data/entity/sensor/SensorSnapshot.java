package org.liara.api.data.entity.sensor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.liara.api.data.entity.ApplicationEntitySnapshot;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SensorSnapshot extends ApplicationEntitySnapshot
{
  @NonNull
  private final String     _name;

  @NonNull
  private final String     _type;

  @Nullable
  private final String     _valueUnit;

  @Nullable
  private final String     _valueLabel;

  @Nullable
  private final String     _ipv4Address;

  @Nullable
  private final String     _ipv6Address;

  @NonNull
  private final List<Long> _states;

  @NonNull
  private final Long       _node;

  public SensorSnapshot(@NonNull final SensorSnapshot toCopy) {
    super(toCopy);
    
    _name = toCopy.getName();
    _type = toCopy.getType();
    _valueUnit = toCopy.getValueUnit();
    _valueLabel = toCopy.getValueLabel();
    _ipv4Address = toCopy.getIpv4Address();
    _ipv6Address = toCopy.getIpv6Address();
    _states = Collections.unmodifiableList(
      toCopy.getStates()
    );
    _node = toCopy.getNode();
  }

  public SensorSnapshot(@NonNull final Sensor model) {
    super(model);
    
    _name = model.getName();
    _type = model.getType();
    _valueUnit = model.getValueUnit();
    _valueLabel = model.getValueLabel();
    _ipv4Address = model.getIpv4Address();
    _ipv6Address = model.getIpv6Address();
    _states = Collections.unmodifiableList(
      model.getStates()
           .stream()
           .map(x -> x.getIdentifier())
           .collect(Collectors.toList())
    );
    _node = model.getNodeIdentifier();
  }

  public String getName () {
    return _name;
  }
  
  public String getType () {
    return _type;
  }
  
  public String getValueUnit () {
    return _valueUnit;
  }
  
  public String getValueLabel () {
    return _valueLabel;
  }
  
  public String getIpv4Address () {
    return _ipv4Address;
  }
  
  public String getIpv6Address () {
    return _ipv6Address;
  }
 
  public List<Long> getStates () {
    return _states;
  }
  
  public Long getNode () {
    return _node;
  }
  
  @Override
  public SensorSnapshot clone () {
    return new SensorSnapshot(this);
  }
  
  @Override
  public Sensor getModel () {
    return (Sensor) super.getModel();
  }
}
