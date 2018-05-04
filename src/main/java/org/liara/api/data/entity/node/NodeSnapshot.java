package org.liara.api.data.entity.node;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.liara.api.data.entity.ApplicationEntitySnapshot;
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
  private final int        _setStart;

  @NonNull
  private final int        _setEnd;

  @NonNull
  private final List<Long> _sensors;

  @NonNull
  private final int        _depth;

  public NodeSnapshot (@NonNull final Node model) {
    super(model);
    
    _name = model.getName();
    _type = model.getType();
    _setStart = model.getSetStart();
    _setEnd = model.getSetEnd();
    _sensors = Collections.unmodifiableList(
      model.getSensors()
           .stream()
           .map(x -> x.getIdentifier())
           .collect(Collectors.toList())
    );
    _depth = model.getDepth();
  }

  public NodeSnapshot (@NonNull final NodeSnapshot toCopy) {
    super(toCopy);

    _name = toCopy.getName();
    _type = toCopy.getType();
    _setStart = toCopy.getSetStart();
    _setEnd = toCopy.getSetEnd();
    _sensors = Collections.unmodifiableList(toCopy.getSensors());
    _depth = toCopy.getDepth();
  }
  
  public String getName () {
    return _name;
  }
  
  public String getType () {
    return _type;
  }
  
  public int getSetStart () {
    return _setStart;
  }
  
  public int getSetEnd() {
    return _setEnd;
  }
  
  public List<Long> getSensors () {
    return _sensors;
  }
  
  public int getDepth () {
    return _depth;
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
