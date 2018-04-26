package org.liara.api.documentation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.liara.api.collection.configuration.CollectionRequestConfiguration;

@Retention(RUNTIME)
@Target(METHOD)
public @interface ParametersFromConfiguration
{
  public Class<? extends CollectionRequestConfiguration<?>> value ();
  
  public boolean orderable () default true;
  public boolean filterable () default true;
  public boolean groupable () default true;
  public boolean cursorable () default true;
}
