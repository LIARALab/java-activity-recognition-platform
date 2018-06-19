package org.liara.api.data.entity.node;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.liara.api.data.entity.ApplicationEntitySnapshot;
import org.liara.api.data.entity.tree.ImmutableNestedSetCoordinates;
import org.liara.api.data.entity.tree.NestedSetCoordinates;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class NodeSnapshot extends ApplicationEntitySnapshot
{
  @NonNull
  private final String     _name;

  @NonNull
  private final String     _type;

  @NonNull
  private final NestedSetCoordinates _coordinates;

  @NonNull
  private final List<Long> _sensors;

  public NodeSnapshot (@NonNull final Node model) {
    super(model);
    
    _name = model.getName();
    _type = model.getType();
    _coordinates = new ImmutableNestedSetCoordinates(model.getCoordinates());
    _sensors = Collections.unmodifiableList(
      model.getSensors()
           .stream()
           .map(x -> x.getIdentifier())
           .collect(Collectors.toList())
    );
  }

  public NodeSnapshot (@NonNull final NodeSnapshot toCopy) {
    super(toCopy);

    _name = toCopy.getName();
    _type = toCopy.getType();
    _coordinates = toCopy.getCoordinates();
    _sensors = Collections.unmodifiableList(toCopy.getSensors());
  }
  
  public String getName () {
    return _name;
  }
  
  public String getType () {
    return _type;
  }
  
  public NestedSetCoordinates getCoordinates () {
    return _coordinates;
  }
  
  public List<Long> getSensors () {
    return _sensors;
  }
  
  @Override
  public NodeSnapshot clone () {
    return new NodeSnapshot(this);
  }
  
  @Override
  public Node getModel () {
    return (Node) super.getModel();
  }
}
