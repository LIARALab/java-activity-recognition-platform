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
  private final String     _unit;


  @NonNull
  private final List<Long> _states;

  @NonNull
  private final Long       _node;

  public SensorSnapshot(@NonNull final SensorSnapshot toCopy) {
    super(toCopy);
    
    _name = toCopy.getName();
    _type = toCopy.getType();
    _unit = toCopy.getUnit();
    _states = Collections.unmodifiableList(
      toCopy.getStates()
    );
    _node = toCopy.getNode();
  }

  public SensorSnapshot(@NonNull final Sensor model) {
    super(model);
    
    _name = model.getName();
    _type = model.getType();
    _unit = model.getUnit();
    
    if (model.getStates() == null || model.getStates().size() == 0) {
      _states = Collections.unmodifiableList(Collections.emptyList());
    } else {
      _states = Collections.unmodifiableList(
        model.getStates()
             .stream()
             .map(x -> x.getIdentifier())
             .collect(Collectors.toList())
      );
    }
    
    _node = model.getNodeIdentifier();
  }

  public String getName () {
    return _name;
  }
  
  public String getType () {
    return _type;
  }
  
  public String getUnit () {
    return _unit;
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
