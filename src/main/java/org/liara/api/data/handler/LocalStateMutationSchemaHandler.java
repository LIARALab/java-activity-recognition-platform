package org.liara.api.data.handler;

import com.google.common.base.Function;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.state.State;
import org.liara.api.data.repository.local.LocalEntityManager;
import org.liara.api.data.schema.StateMutationSchema;
import org.springframework.lang.NonNull;

import java.util.Map;

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
    if (schema.getEmittionDate() != null) state.setEmissionDate(schema.getEmittionDate());
    
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
    final State oldValue = _manager.find(input.getState()).get().clone();
    final State result   = resolve(input);
    result.setIdentifier(oldValue.getIdentifier());
    _manager.merge(result);
    return result;
  }
}
