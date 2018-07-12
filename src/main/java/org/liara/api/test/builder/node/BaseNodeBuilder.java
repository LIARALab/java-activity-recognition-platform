package org.liara.api.test.builder.node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.node.NodeCreationSchema;
import org.liara.api.data.entity.tree.LocalNestedSetTree;
import org.liara.api.test.builder.entity.BaseEntityBuilder;
import org.liara.api.test.builder.sensor.BaseSensorBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract class BaseNodeBuilder<Self extends BaseNodeBuilder<Self>> 
                extends BaseEntityBuilder<Self>
{  
  @Nullable
  private String _name;
  
  @Nullable
  private String _type;
  
  @NonNull
  private final Set<BaseSensorBuilder<?>> _sensors = new HashSet<>();
  
  public Self withType (@Nullable final String type) {
    _type = type;
    return self();
  }
  
  public Self withName (@Nullable final String name) {
    _name = name;
    return self();
  }
  
  public <SubBuilder extends BaseSensorBuilder<?>> SubBuilder with (
    @NonNull final SubBuilder builder
  ) {
    _sensors.add(builder);
    return builder;
  }
  
  public String getName () {
    return _name;
  }

  public String getType () {
    return _type;
  }
  
  public Set<BaseSensorBuilder<?>> getSensors () {
    return Collections.unmodifiableSet(_sensors);
  }
  
  public NodeCreationSchema buildSchema () {
    final NodeCreationSchema schema = new NodeCreationSchema();
    schema.setName(_name);
    schema.setType(_type);
    
    return schema;
  }
  
  public Node build () {
    return build(new LocalNestedSetTree<>());
  }
  
  public Node build (@NonNull final LocalNestedSetTree<Node> tree) {
    final Node result = new Node(tree, buildSchema());
    
    super.apply(result);
    
    for (final BaseSensorBuilder<?> sensor : _sensors) {
      result.addSensor(sensor.build());
    }
    
    return result;
  }
}
