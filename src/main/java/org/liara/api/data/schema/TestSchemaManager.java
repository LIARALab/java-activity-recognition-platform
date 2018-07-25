package org.liara.api.data.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.liara.api.utils.Beans;
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
    
    if (handler == null) {
      return null;
    } else {
      return (Entity) handler.apply(schema);
    }
  }
  
  public int getHandledSchemaCount () {
    return _handledSchemas.size();
  }
  
  public Object getHandledSchema (final int index) {
    return _handledSchemas.get(index);
  }
  
  public Object getAt (final int index) {
    return getHandledSchema(index);
  }
  
  public <HandledSchema> HandledSchema getHandledSchema (
    final int index,
    @NonNull final Class<? extends HandledSchema> type
  ) {
    return type.cast(getHandledSchema(index));
  }

  
  public <HandledSchema> HandledSchema getAt (
    final int index,
    @NonNull final Class<? extends HandledSchema> type
  ) {
    return getHandledSchema(index, type);
  }
  
  public boolean hasHandledSchemaOfType (
    final int index,
    @NonNull final Class<?> type
  ) {
    return type.isInstance(getHandledSchema(index));
  }
  
  public List<Object> getHandledSchemas () {
    return _handledSchemas;
  }
  
  public void reset () {
    _handledSchemas.clear();
  }
  
  public boolean hasHandled (@NonNull final List<Map<String, ?>> descriptors) {
    if (getHandledSchemaCount() < descriptors.size()) return false;
    
    for (int index = 0; index < descriptors.size(); ++index) {
      final Map<String, ?> descriptor = descriptors.get(index);
      
      if (descriptor.containsKey("class")) {
        final Class<?> type = (Class<?>) descriptor.get("class");
        if (hasHandledSchemaOfType(index, type) == false) {
          return false;
        }
      }
      
      if (Beans.lookLike(getHandledSchema(index), descriptor) == false) {
        return false;
      }
    }
    
    return true;
  }

  @Override
  public void begin () { }

  @Override
  public void finish () { }

  @Override
  public void rollback () { }

  @Override
  public void flush () { }

  @Override
  public void clear () { }
}
