package org.liara.api.test.builder.node;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.tree.NestedSetTree;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.liara.api.test.builder.Builder;
import org.liara.api.test.builder.IdentityBuilder;
import org.liara.api.test.builder.entity.BaseApplicationEntityBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.google.common.collect.Streams;

public abstract class BaseNodeBuilder<
                        Self extends BaseNodeBuilder<Self, Entity>,
                        Entity extends Node
                      > 
                extends BaseApplicationEntityBuilder<Self, Entity>
{  
  @Nullable
  private NestedSetTree<Node> _tree;
  
  @Nullable
  private String _name;
  
  @Nullable
  private String _type;
  
  @NonNull
  private final Set<Builder<?, ? extends Sensor>> _sensors = new HashSet<>();
  
  public Self withTree (@Nullable final NestedSetTree<Node> tree) {
    _tree = tree;
    return self();
  }
  
  public Self withType (@Nullable final String type) {
    _type = type;
    return self();
  }
  
  public Self withName (@Nullable final String name) {
    _name = name;
    return self();
  }
  
  public <SubBuilder extends Builder<?, ? extends Sensor>> Self withSensor (
    @NonNull final SubBuilder builder
  ) {
    _sensors.add(builder);
    return self();
  }
  
  public Self withSensor (@NonNull final Sensor sensor) {
    return withSensor(IdentityBuilder.of(sensor));
  }
  
  public String getName () {
    return _name;
  }

  public String getType () {
    return _type;
  }
  
  public Set<Builder<?, ? extends Sensor>> getSensors () {
    return Collections.unmodifiableSet(_sensors);
  }
  
  public void apply (@NonNull final Entity node) {
    super.apply(node);
    node.setName(_name);
    node.setType(_type);
    node.setTree(_tree);
    
    for (final Builder<?, ? extends Sensor> sensor : _sensors) {
      node.addSensor(sensor.build());
    }
  }

  @Override
  public Entity buildFor (@NonNull final LocalEntityManager entityManager) {
    final Entity result = super.buildFor(entityManager);
    entityManager.addAll(result.sensors());
    Streams.stream(result.sensors()).forEach(x -> entityManager.addAll(x.states()));
    return result;
  }
}
