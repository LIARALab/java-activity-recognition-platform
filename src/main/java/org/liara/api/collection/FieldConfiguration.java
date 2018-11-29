package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.collection.operator.Operator;
import org.liara.request.parser.APIRequestFieldParser;
import org.liara.request.validator.APIRequestFieldValidator;

import java.util.Optional;
import java.util.Set;

public interface FieldConfiguration
{
  @NonNull Set<@NonNull String> getFields ();

  boolean containsField (@NonNull final String name);

  @NonNull Optional<APIRequestFieldParser<Operator>> getFieldParser (@NonNull final String name);

  @NonNull Optional<APIRequestFieldValidator> getFieldValidator (@NonNull final String name);
}
