package org.liara.api.test.builder.state;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.liara.api.test.builder.Builder;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import groovy.lang.Closure;

public class StateSequenceBuilder implements Builder<StateSequenceBuilder, List<State>>
{
  public static StateSequenceBuilder create (@NonNull final Closure<?> configurator) {
    final StateSequenceBuilder result = new StateSequenceBuilder();
    Closures.callAs(configurator, result);
    return result;
  }
  
  public static StateSequenceBuilder create () {
    final StateSequenceBuilder result = new StateSequenceBuilder();
    return result;
  }
  
  @Nullable
  private BaseStateBuilder<?, ? extends State> _beforeLastDefinedState = null;
  
  @Nullable
  private BaseStateBuilder<?, ? extends State> _lastDefinedState = null;

  @NonNull
  private final List<BaseStateBuilder<?, ? extends State>> _states = new ArrayList<>();
  
  public <SubBuilder extends BaseStateBuilder<?, ? extends State>> StateSequenceBuilder with (
    @NonNull final SubBuilder builder
  ) {
    _states.add(builder);
    _beforeLastDefinedState = _lastDefinedState;
    _lastDefinedState = builder;
    return self();
  }
  
  public <SubBuilder extends BaseStateBuilder<?, ? extends State>> StateSequenceBuilder andWith (
    @NonNull final SubBuilder builder
  ) {
    return with(builder);
  }
  
  public StateSequenceBuilder after (
    final int duration,
    @NonNull final TemporalUnit unit
  ) {
    if (_beforeLastDefinedState == null) {
      throw new IllegalStateException(String.join(
        "",
        "Invalid invocation of after : the current sensor builder ",
        "does not have any previous state registered in."
      ));
    }
    
    _lastDefinedState.withEmittionDate(
      _beforeLastDefinedState.getEmittionDate().plus(duration, unit)
    );
    
    return self();
  }
  
  public StateSequenceBuilder afterMilliseconds (final int duration) {
    return after(duration, ChronoUnit.MILLIS);
  }
  
  public StateSequenceBuilder afterSeconds (final int duration) {
    return after(duration, ChronoUnit.SECONDS);
  }
  
  public StateSequenceBuilder afterMinutes (final int duration) {
    return after(duration, ChronoUnit.MINUTES);
  }
  
  public StateSequenceBuilder afterHours (final int duration) {
    return after(duration, ChronoUnit.HOURS);
  }
  
  public StateSequenceBuilder afterDays (final int duration) {
    return after(duration, ChronoUnit.DAYS);
  }
  
  public StateSequenceBuilder afterMonths (final int duration) {
    return after(duration, ChronoUnit.MONTHS);
  }
  
  public StateSequenceBuilder afterYears (final int duration) {
    return after(duration, ChronoUnit.YEARS);
  }
  
  public StateSequenceBuilder at (@NonNull final ZonedDateTime date) {
    if (_lastDefinedState == null) {
      throw new IllegalStateException(String.join(
        "",
        "Invalid call to at : the current builder instance does not ",
        "have any state added yet."
      ));
    }
    
    _lastDefinedState.withEmittionDate(date);
    
    return self();
  }

  @Override
  public List<State> build () {
    return _states.stream().map(BaseStateBuilder::build)
                           .collect(Collectors.toList());
  }
  
  public List<State> buildFor (@NonNull final LocalEntityManager manager) {
    final List<State> result = build();
    manager.addAll(result);
    return result;
  }

  @Override
  public StateSequenceBuilder self () {
    return this;
  }
}
