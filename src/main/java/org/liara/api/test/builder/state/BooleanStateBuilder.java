package org.liara.api.test.builder.state;

import org.liara.api.data.entity.state.BooleanState;
import org.liara.api.data.repository.local.ApplicationEntityIdentifiers;
import org.liara.api.utils.Closures;
import org.springframework.lang.NonNull;

import groovy.lang.Closure;

public class BooleanStateBuilder 
       extends BaseStateBuilder<BooleanStateBuilder, BooleanState>
{
  public static BooleanStateBuilder truthy () {
    return new BooleanStateBuilder().withValue(true);
  }
  
  public static BooleanStateBuilder truthy (@NonNull final Closure<?> closure) {
    final BooleanStateBuilder result = new BooleanStateBuilder().withValue(true);
    
    Closures.callAs(closure, result);
    
    return result;
  }

  public static BooleanStateBuilder truthy (@NonNull final ApplicationEntityIdentifiers identifiers) {
    return new BooleanStateBuilder().withValue(true)
                                    .withIdentifier(identifiers.next(BooleanState.class));
  }
  
  public static BooleanStateBuilder falsy () {
    return new BooleanStateBuilder().withValue(false);
  }
  
  public static BooleanStateBuilder falsy (@NonNull final ApplicationEntityIdentifiers identifiers) {
    return new BooleanStateBuilder().withValue(false)
                                    .withIdentifier(identifiers.next(BooleanState.class));
  }
  
  public static BooleanStateBuilder falsy (@NonNull final Closure<?> closure) {
    final BooleanStateBuilder result = new BooleanStateBuilder().withValue(false);
    
    Closures.callAs(closure, result);
    
    return result;
  }
  
  public static BooleanStateBuilder create (@NonNull final Closure<?> closure) {
    final BooleanStateBuilder result = new BooleanStateBuilder();
    Closures.callAs(closure, result);
    return result;
  }
  
  private boolean _value = false;
  
  public BooleanStateBuilder withValue (final boolean value) {
    _value = value;
    return self();
  }
  
  protected void apply (@NonNull final BooleanState state) {
    super.apply(state);
    state.setValue(_value);
  }
  
  @Override
  public BooleanState build () {
    final BooleanState state = new BooleanState();
    
    apply(state);
    
    return state;
  }

  @Override
  public BooleanStateBuilder self () {
    return this;
  }
}
