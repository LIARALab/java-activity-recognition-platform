package org.domus.api;

import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.GraphQLSchema;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.lang.ClassLoader;
import java.io.File;

import org.domus.api.fetcher.SensorFetcher;

public final class APIGraphQLFactory {
  private static TypeDefinitionRegistry TYPE_REGISTRY = null;
  private static RuntimeWiring RUNTIME_WIRING = null;
  private static SchemaGenerator GENERATOR = new SchemaGenerator();

  private static String[] RESOURCES = new String[] {
    /*"scalars.graphql",*/
    "Sensor.graphql",
    "Query.graphql",
    "Schema.graphql"
  };

  private static void loadGraphQLTypes () {
    SchemaParser parser = new SchemaParser();
    Class<?> loader = APIGraphQLFactory.class;

    APIGraphQLFactory.TYPE_REGISTRY = new TypeDefinitionRegistry();

    for (final String resource : APIGraphQLFactory.RESOURCES) {
      try {
        APIGraphQLFactory.TYPE_REGISTRY.merge(
          parser.parse(
            new BufferedReader(new InputStreamReader(
              loader.getResource(resource).openStream()
            ))
          )
        );
      } catch (Exception e) {
        throw new Error(e);
      }

    }
  }

  private static void doWiring () {
    APIGraphQLFactory.RUNTIME_WIRING = RuntimeWiring.newRuntimeWiring().type(
      "Query",
      typeWiring ->
        typeWiring.dataFetcher("sensor", new SensorFetcher())
    ).build();
  }

  public static GraphQLSchema create () {
    if (APIGraphQLFactory.TYPE_REGISTRY == null) {
      APIGraphQLFactory.loadGraphQLTypes();
    }

    if (APIGraphQLFactory.RUNTIME_WIRING == null) {
      APIGraphQLFactory.doWiring();
    }

    return APIGraphQLFactory.GENERATOR.makeExecutableSchema(
      APIGraphQLFactory.TYPE_REGISTRY,
      APIGraphQLFactory.RUNTIME_WIRING
    );
  }
}
