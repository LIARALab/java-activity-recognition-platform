package org.liara.api.test.builder.state;

import java.time.ZonedDateTime;

import org.liara.api.data.entity.state.ActivityState;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import groovy.lang.Closure;

public class ActivityStateBuilder 
      extends BaseStateBuilder<ActivityStateBuilder, ActivityState>
{
  public static ActivityStateBuilder create (@NonNull final Closure<?> closure) {
    final ActivityStateBuilder result = new ActivityStateBuilder();
    Closures.callAs(closure, result);
    return result;
  }
  
  private ZonedDateTime _start = null;
  private ZonedDateTime _end = null;
  private String _tag = null;
  
  public ActivityStateBuilder withStart (
    @Nullable final ZonedDateTime start
  ) {
    _start = start;
    return self();
  }
  
  public ActivityStateBuilder withEnd (
    @Nullable final ZonedDateTime end
  ) {
    _end = end;
    return self();
  }
  
  public ActivityStateBuilder withTag (
    @Nullable final String tag
  ) {
    _tag = tag;
    return self();
  }
  
  protected void apply (
    @NonNull final ActivityState state
  ) {
    super.apply(state);
    state.setStart(_start);
    state.setEnd(_end);
    state.setTag(_tag);
  }
  
  @Override
  public ActivityState build () {
    final ActivityState state = new ActivityState();
    
    apply(state);
    
    return state;
  }

  @Override
  public ActivityStateBuilder self () {
    return this;
  }
}
