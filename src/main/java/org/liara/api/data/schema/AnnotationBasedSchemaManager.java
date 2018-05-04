package org.liara.api.data.schema;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

@Service
public class AnnotationBasedSchemaManager implements SchemaManager
{
  @NonNull
  private final ApplicationContext _context;
  
  @NonNull
  private final BiMap<Class<?>, String> _handlers = HashBiMap.create();

  @Autowired
  public AnnotationBasedSchemaManager (
    @NonNull final ApplicationContext context
  ) {
    _context = context;
  }
  
  @EventListener
  public void captureHandlers (@NonNull final ContextStartedEvent event) {
    captureHandlers();
  }
  
  @EventListener
  public void captureHandlers (@NonNull final ContextRefreshedEvent event) {
    captureHandlers();
  }
  
  private void captureHandlers () {
    _handlers.clear();
    
    final String[] handlerNames = _context.getBeanNamesForAnnotation(SchemaHandler.class);
    
    for (final String handlerName : handlerNames) {
      final Class<?>[] handleds = _context.getType(handlerName).getAnnotation(SchemaHandler.class).value();
      
      for (final Class<?> handled : handleds) {
        if (_handlers.containsKey(handled)) {
          throw new Error(String.join(
            "",
            "Unnable to initialize ", this.getClass().toString(), " because ",
            "two handler class was registered for the schema class ", handled.toString(),
            " : ", _context.getType(handlerName).toString(), " and ",
            _context.getType(_handlers.get(handled)).toString()
          ));
        } else {
          _handlers.put(handled, handlerName);
        }
      }
    }
  }

  @Override
  public <Entity> Entity execute (@NonNull final Object schema) {
    assertIsValidSchema(schema);
    
    final String handlerName = findHandlerForSchemaOrFail(schema);
    final Method method = getHandlerMethodOrFail(handlerName);
    
    final Object handler = _context.getBean(handlerName);
    final Object result = executeOrFail(method, handler, schema);
    
    return castOrFail(result);
  }

  private Object executeOrFail (
    @NonNull final Method method, 
    @NonNull final Object handler, 
    @NonNull final Object schema
  ) {
    try {
      return method.invoke(handler, schema);
    } catch (final Exception exception) {
      throw new Error(String.join(
        "", 
        "Unnable to execute the given schema ", schema.getClass().toString(),
        " with the handler ", handler.getClass().toString(), " because the call to ",
        " the handler method as failed."
      ), exception);
    }
  }

  @SuppressWarnings("unchecked")
  private <Entity> Entity castOrFail (@NonNull final Object result) {
    try {
      return (Entity) result;
    } catch (final ClassCastException exception) {
      throw new Error(String.join(
        "",
        "Invalid return type requested for the result of the ",
        "execution of the given schema."
      ), exception);
    }
  }

  private Method getHandlerMethodOrFail (@NonNull final String handlerName) {
    final Class<?> handledSchema = _handlers.inverse().get(handlerName);
    
    try {
      return _context.getType(handlerName).getMethod("handle", handledSchema);
    } catch (final Exception exception) {
      throw new Error(String.join(
        "", 
        "Invalid schema handler of type ", handlerName,
        " for schemas of type ", handledSchema.toString(),
        " because the given handler does not declare a valid public",
        " method that accept an unique parameter of type ",
        handledSchema.toString(), "."
      ), exception);
    }
  }

  private String findHandlerForSchemaOrFail (@NonNull final Object schema) {
    final Class<?> schemaClass = schema.getClass();
    String result = null;
    Class<?> resultHandledSchemaClass = null;
    
    for (final Map.Entry<Class<?>, String> entry : _handlers.entrySet()) {
      final Class<?> handledSchemaClass = entry.getKey();
      final String handler = entry.getValue();
      if (
        handledSchemaClass.isAssignableFrom(schemaClass) && (
          resultHandledSchemaClass == null || 
          resultHandledSchemaClass.isAssignableFrom(handledSchemaClass)
        )
      ) {
        result = handler;
        resultHandledSchemaClass = handledSchemaClass;
      }
    }
    
    if (result == null) {
      throw new Error(String.join(
        "", 
        "Unnable to execute the given schema of type ",
        schema.getClass().toString(), " because it does ",
        "not exists a registered handler hable to handle this ",
        "schema class."
      ));
    }
    
    return result;
  }

  private void assertIsValidSchema (
    @NonNull final Object schema
  ) {
    if (schema.getClass().getAnnotation(Schema.class) == null) {
      throw new Error(String.join(
        "", 
        "Unnable to execute the given object of type ",
        schema.getClass().toString(), " because the given",
        " instance is not a valid schema class : a valid schema",
        " class must be annotated with a schema annotation."
      ));
    }
  }
}
