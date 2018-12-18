package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface CollectionController
{
  @NonNull String name ();

  @NonNull Class<?> managedType ();
}
