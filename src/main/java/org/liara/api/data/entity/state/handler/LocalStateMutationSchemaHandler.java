package org.liara.api.data.entity.state.handler;

import java.util.Map;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.entity.state.StateMutationSchema;
import org.liara.api.data.entity.state.StateSnapshot;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.springframework.lang.NonNull;

import com.google.common.base.Function;

public class LocalStateMutationSchemaHandler<Schema extends StateMutationSchema>
       implements Function<Schema, State>
{
  @NonNull
  private final LocalEntityManager _manager;
  
  public LocalStateMutationSchemaHandler (
    @NonNull final LocalEntityManager manager
  ) {
    _manager = manager;
  }

  public void apply (@NonNull final StateMutationSchema schema, @NonNull final State state) {
    if (schema.getEmittionDate() != null) state.setEmittionDate(schema.getEmittionDate());
    
    for (final String decorrelation : schema.getDecorrelations()) {
      state.decorrelate(decorrelation);
    }
    
    for (final Map.Entry<String, ApplicationEntityReference<State>> correlation : schema.getCorrelations().entrySet()) {
      state.correlate(
        correlation.getKey(), 
        _manager.find(correlation.getValue()).get()
      );
    }
  }
  
  public State resolve (@NonNull final Schema input) {
    final State result = _manager.find(input.getState()).get();
    _manager.remove(result);
    apply(input, result);
    return result;
  }
  
  @Override
  public State apply (@NonNull final Schema input) {
    final StateSnapshot oldValue = _manager.find(input.getState()).get().snapshot();
    final State result = resolve(input);
    result.setIdentifier(oldValue.getIdentifier());
    _manager.merge(result);
    return result;
  }
}
