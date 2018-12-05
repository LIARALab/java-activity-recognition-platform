package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.api.collection.configuration.RequestConfiguration;
import org.liara.collection.jpa.JPAEntityCollection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

public interface CollectionController<Entity>
{
  static @NonNull String getName (@NonNull final CollectionController<?> controller) {
    return controller.getClass().getAnnotation(CollectionController.Name.class).value();
  }

  @NonNull RequestConfiguration getRequestConfiguration ();

  @NonNull JPAEntityCollection<Entity> getCollection ();

  @Retention(RetentionPolicy.RUNTIME)
  @Target({TYPE})
  @interface Name
  {
    @NonNull String value ();
  }
}
