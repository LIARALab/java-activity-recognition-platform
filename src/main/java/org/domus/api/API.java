package org.domus.api;

import graphql.schema.GraphQLSchema;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

public class API {
  public static void main(String[] args) {
    GraphQLSchema schema = APIGraphQLFactory.create();

    GraphQL query = GraphQL.newGraphQL(schema)
                           .build();

    String queryString = String.join(
      "\r\n",
      "query { ",
      "  first: sensor (identifier: 5) {",
      "    identifier,",
      "    name",
      "  },",
      "  second: sensor (identifier: 10) {",
      "    identifier,",
      "    name",
      "  }",
      "}"
    );

    ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                                                  .query(queryString)
                                                  .build();

    ExecutionResult executionResult = query.execute(executionInput);

    System.out.println(executionResult.getData().toString());
  }
}
