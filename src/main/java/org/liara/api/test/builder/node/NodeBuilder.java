package org.liara.api.test.builder.node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.liara.api.data.entity.node.Node;
import org.liara.api.data.entity.node.NodeCreationSchema;
import org.liara.api.data.schema.SchemaManager;
import org.liara.api.test.builder.sensor.MotionSensorBuilder;
import org.liara.api.test.builder.sensor.SensorBuilder;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import groovy.lang.Closure;

public abstract class NodeBuilder<Self extends NodeBuilder<Self>>
{
  private static <SubBuilder extends NodeBuilder<?>> SubBuilder create (
    @NonNull final SubBuilder builder,
    @NonNull final Closure<?> closure
   ) {
    Closures.callAs(closure, builder);
    return builder;
  }
  
  public static HouseNodeBuilder createHouse (
    @NonNull final Closure<?> closure
  ) {
    return NodeBuilder.create(new HouseNodeBuilder(), closure);
  }
  
  public static HouseNodeBuilder createHouse () {
    return new HouseNodeBuilder();
  }
  
  public static RoomNodeBuilder createRoom (
    @NonNull final Closure<?> closure
  ) {
    return NodeBuilder.create(new RoomNodeBuilder(), closure);
  }
  
  public static RoomNodeBuilder createRoom () {
    return new RoomNodeBuilder();
  }
  
  public static FurnitureNodeBuilder createFurniture (
    @NonNull final Closure<?> closure
  ) {
    return NodeBuilder.create(new FurnitureNodeBuilder(), closure);
  }
  
  public static FurnitureNodeBuilder createFurniture () {
    return new FurnitureNodeBuilder();
  }
  
  @NonNull
  private String _name;
  
  @Nullable
  private Node _parent;
  
  @NonNull
  private final String _type;
  
  @NonNull
  private final List<SensorBuilder<?>> _sensors = new ArrayList<>();
  
  public NodeBuilder (@NonNull final String type) {
    _type = type;
    _name = "unnamed";
    _parent = null;
  }
  
  public Self withName (@NonNull final String name) {
    _name = name;
    return self();
  }
  
  public Self withParent (@Nullable final Node parent) {
    _parent = parent;
    return self();
  }
  
  public <SubBuilder extends SensorBuilder<?>> SubBuilder withSensor (
    @NonNull final SubBuilder builder
  ) {
    _sensors.add(builder);
    return builder;
  }
  
  public <SubBuilder extends SensorBuilder<?>> Self withSensor (
    @NonNull final SubBuilder builder,
    @NonNull final Function<SubBuilder, Void> callback
  ) {
    _sensors.add(builder);
    callback.apply(builder);
    return self();
  }
  
  public <SubBuilder extends SensorBuilder<?>> Self withSensor (
    @NonNull final SubBuilder builder,
    @NonNull final Closure<?> closure
  ) {
    _sensors.add(builder);
    Closures.callAs(closure, builder);
    return self();
  }
  
  public MotionSensorBuilder withMotionSensor () {
    return withSensor(SensorBuilder.createMotionSensor());
  }
  
  public Self withMotionSensor (
    @NonNull final Function<MotionSensorBuilder, Void> callback
  ) {
    return withSensor(SensorBuilder.createMotionSensor(), callback);
  }
  
  public Self withMotionSensor (
    @NonNull final Closure<?> callback
  ) {
    return withSensor(SensorBuilder.createMotionSensor(), callback);
  }
  
  public String getName () {
    return _name;
  }

  public Node getParent () {
    return _parent;
  }

  public String getType () {
    return _type;
  }
  
  public List<SensorBuilder<?>> getSensors () {
    return _sensors;
  }
  
  public Self willBeBuild () {
    return self();
  }
  
  public Node into (@NonNull final SchemaManager manager) {
    return build(manager);
  }

  public Node build (@NonNull final SchemaManager manager) {
    final NodeCreationSchema schema = new NodeCreationSchema();
    schema.setName(_name);
    schema.setType(_type);
    schema.setParent(_parent);
    
    final Node result = manager.execute(schema);
    
    for (final SensorBuilder<?> sensor : _sensors) {
      sensor.withParent(result);
      sensor.build(manager);
    }
    
    return result;
  }
  
  protected abstract Self self();
}
