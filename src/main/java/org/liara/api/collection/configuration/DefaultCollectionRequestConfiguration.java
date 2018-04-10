package org.liara.api.collection.configuration;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface DefaultCollectionRequestConfiguration
{
  public Class<? extends CollectionRequestConfiguration<?>> value ();
}
