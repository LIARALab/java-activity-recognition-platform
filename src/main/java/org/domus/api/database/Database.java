package org.domus.api.database;

import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.domus.api.data.Sensor;

public final class Database {
  private static BootstrapServiceRegistry BOOTSTRAP_REGISTRY;
  private static StandardServiceRegistry STANDARD_REGISTRY;
  private static MetadataSources METADATA_SOURCES;
  private static Metadata METADATA;
  private static SessionFactory SESSION_FACTORY;

  public static SessionFactory sessions () {
    if (Database.SESSION_FACTORY == null) {
      Database.bootstrap();
    }

    return Database.SESSION_FACTORY;
  }

  private static void bootstrap () {
    Database.BOOTSTRAP_REGISTRY = new BootstrapServiceRegistryBuilder().build();
    Database.STANDARD_REGISTRY = new StandardServiceRegistryBuilder(
      Database.BOOTSTRAP_REGISTRY
    ).configure(
      Database.class.getResource("configuration.xml")
    ).build();

    Database.METADATA_SOURCES = new MetadataSources(Database.STANDARD_REGISTRY);
    Database.METADATA_SOURCES.addAnnotatedClass(Sensor.class);
    Database.METADATA = Database.METADATA_SOURCES.buildMetadata();

    Database.SESSION_FACTORY = Database.METADATA.getSessionFactoryBuilder()
                                                .build();
  }
}
