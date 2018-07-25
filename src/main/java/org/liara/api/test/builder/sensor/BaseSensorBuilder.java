package org.liara.api.test.builder.sensor;

import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.test.builder.Builder;
import org.liara.api.test.builder.IdentityBuilder;
import org.liara.api.test.builder.entity.BaseApplicationEntityBuilder;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.google.common.collect.Streams;

import groovy.lang.Closure;

public abstract class BaseSensorBuilder<
                        Self extends BaseSensorBuilder<Self, Entity>,
                        Entity extends Sensor
                      >
                extends BaseApplicationEntityBuilder<Self, Entity>
{
  public static SensorBuilder createMotionSensor (
    @NonNull final Closure<?> closure
  ) {
    final SensorBuilder builder = new SensorBuilder();
    Closures.callAs(closure, builder);
    return builder;
  }
  
  public static SensorBuilder createMotionSensor () {
    return new SensorBuilder();
  }

  @Nullable
  private String _type = null;
  
  @Nullable
  private String _name = null;
  
  @Nullable
  private String _unit = null;
  
  @Nullable
  private SensorConfiguration _configuration = null;
  
  @NonNull
  private final Set<Builder<?, ? extends State>> _states = new HashSet<>();
  
  public Self withType (@Nullable final Class<?> type) {
    _type = type.getName();
    return self();
  }
  
  public Self withName (@Nullable final String name) {
    _name = name;
    return self();
  }
  
  public Self withUnit (@Nullable final String unit) {
    _unit = unit;
    return self();
  }
  
  public Self withConfiguration (@Nullable final SensorConfiguration configuration) {
    _configuration = configuration;
    return self();
  }
  
  public Self withRawStates (@NonNull final Iterable<State> states) {
    Streams.stream(states).map(x -> IdentityBuilder.of(x))
                          .forEach(_states::add);
    return self();
  }
  
  public Self withStates (@NonNull final Iterable<Builder<?, State>> states) {
    Streams.stream(states).forEach(_states::add);
    return self();
  }
  
  public Self withState (@NonNull final Builder<?, State> state) {
    _states.add(state);
    return self();
  }
  
  public Self withRawState (@NonNull final State state) {
    _states.add(IdentityBuilder.of(state));
    return self();
  }
  
  public void apply (@NonNull final Entity sensor) {    
    super.apply(sensor);
    
    sensor.setName(_name);
    sensor.setType(_type);
    sensor.setUnit(_unit);
    sensor.setConfiguration(_configuration);
    
    for (final Builder<?, ? extends State> state : _states) {
      sensor.addState(state.build());
    }
  }

  @Override
  public Entity buildFor (@NonNull final LocalEntityManager entityManager) {
    final Entity result = super.buildFor(entityManager);
    entityManager.addAll(result.states());
    return result;
  }
}
