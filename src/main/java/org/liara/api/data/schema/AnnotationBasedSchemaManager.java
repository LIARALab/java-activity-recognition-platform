package org.liara.api.data.schema;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
@Primary
public class AnnotationBasedSchemaManager implements SchemaManager
{
  @NonNull
  private final ApplicationContext _context;
  
  @NonNull
  private final EntityManager _entityManager;
  
  @Nullable
  private EntityManager _current;
  
  @NonNull
  private final EntityManagerFactory _entityManagerFactory;
  
  @NonNull
  private final BiMap<Class<?>, String> _handlers = HashBiMap.create();
  
  @NonNull
  private final Map<Class<?>, Class<?>> _handlerSolutions = new HashMap<>();
  
  @Autowired
  public AnnotationBasedSchemaManager (
    @NonNull final ApplicationContext context
  ) {
    _context = context;
    _entityManager = context.getBean(EntityManager.class);
    _entityManagerFactory = context.getBean(EntityManagerFactory.class);
    _current = null;
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
    _handlerSolutions.clear();
    
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
          _handlerSolutions.put(handled, handled);
        }
      }
    }
  }
  
  public void begin () {
    if (_current != null) {
      finish();
    }
    
    _current = _entityManagerFactory.createEntityManager();
    _current.getTransaction().begin();
  }
  
  public void finish () {
    _current.getTransaction().commit();
    _current.close();
    _current = null;
  }
  
  public void rollback () {
    _current.getTransaction().rollback();
    _current.close();
    _current = null;
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
      return method.invoke(
        handler,
        _current == null ? _entityManager : _current,
        schema
      );
    } catch (final Exception exception) {
      throw new Error(String.join(
        "",
        "Unable to execute the given schema ", schema.getClass().toString(),
        " with the handler ", handler.getClass().toString(), " because the call to",
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

  protected Method getHandlerMethodOrFail (@NonNull final String handlerName) {
    final Class<?> handledSchema = _handlers.inverse().get(handlerName);
    
    try {
      return _context.getType(handlerName).getMethod("handle", EntityManager.class, handledSchema);
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

  protected String findHandlerForSchemaOrFail (@NonNull final Object schema) {
    final Class<?> schemaClass = schema.getClass();
    
    if (_handlerSolutions.containsKey(schemaClass)) {
      return _handlers.get(_handlerSolutions.get(schemaClass));
    }
    
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
    
    _handlerSolutions.put(schemaClass, resultHandledSchemaClass);
    return result;
  }

  protected void assertIsValidSchema (
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

  @Override
  public void flush () {
    if (_current == null) {
      _entityManager.flush();
    } else _current.flush();
  }

  @Override
  public void clear () {
    if (_current == null) {
      _entityManager.clear();
    } else _current.clear();
  }
}
