package org.liara.api.resource;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.resource.collection.*;
import org.liara.rest.metamodel.RestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Component("rootResource")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class RootResource
  implements RestResource
{
  @NonNull
  private final Map<@NonNull String, @NonNull RestResource> _resources;

  @Autowired
  public RootResource (
    @NonNull final ApplicationContext applicationContext
  ) {
    _resources = new HashMap<>();

    _resources.put("nodes", applicationContext.getBean(NodeCollection.class));
    _resources.put("sensors", applicationContext.getBean(SensorCollection.class));
    _resources.put("states", applicationContext.getBean(StateCollection.class));
    _resources.put("states:boolean", applicationContext.getBean(BooleanStateCollection.class));
    _resources.put("states:byte", applicationContext.getBean(ByteStateCollection.class));
    _resources.put("states:double", applicationContext.getBean(DoubleStateCollection.class));
    _resources.put("states:float", applicationContext.getBean(FloatStateCollection.class));
    _resources.put("states:integer", applicationContext.getBean(IntegerStateCollection.class));
    _resources.put("states:label", applicationContext.getBean(LabelStateCollection.class));
    _resources.put("states:long", applicationContext.getBean(LongStateCollection.class));
    _resources.put("states:short", applicationContext.getBean(ShortStateCollection.class));
    _resources.put("states:string", applicationContext.getBean(StringStateCollection.class));
  }

  @Override
  public boolean hasResource (@NonNull final String name) {
    return _resources.containsKey(name.toLowerCase());
  }

  @Override
  public @NonNull RestResource getResource (
    @NonNull final String name
  )
  throws NoSuchElementException {
    @Nullable final RestResource resource = _resources.computeIfAbsent(
      name, x -> null
    );

    return resource == null ? RestResource.super.getResource(name)
                            : resource
      ;
  }
}
