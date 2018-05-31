package org.liara.api.data.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class TestSchemaManager implements SchemaManager
{
  private List<Object> _handledSchemas = new ArrayList<>();
  
  private Map<Class<?>, Function<Object, Object>> _handlers = new HashMap<>();

  @SuppressWarnings("unchecked")
  public <HandledSchema> void registerHandler (
    @NonNull final Class<HandledSchema> type,
    @NonNull final Function<HandledSchema, Object> handler
  ) {
    _handlers.put(type, (Object object) -> handler.apply((HandledSchema) object));
  }
  
  public Function<Object, Object> getHandlerFor (@NonNull final Object schema) {
    final Class<?> schemaClass = schema.getClass();
    Function<Object, Object> result = (Object handled) -> { return null; };
    Class<?> handledClass = null;
    
    for (final Map.Entry<Class<?>, Function<Object, Object>> entry : _handlers.entrySet()) {
      if (entry.getKey().isAssignableFrom(schemaClass)) {
        if (handledClass == null || handledClass.isAssignableFrom(entry.getKey())) {
          handledClass = entry.getKey();
          result = entry.getValue();
        }
      }
    }
    
    return result;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <Entity> Entity execute (@NonNull final Object schema) {
    _handledSchemas.add(schema);
    final Function<Object, Object> handler = getHandlerFor(schema);
    
    return (Entity) handler.apply(schema);
  }
  
  public int getHandledSchemaCount () {
    return _handledSchemas.size();
  }
  
  public List<Object> getHandledSchemas () {
    return _handledSchemas;
  }
  
  public void clear () {
    _handledSchemas.clear();
  }
}
