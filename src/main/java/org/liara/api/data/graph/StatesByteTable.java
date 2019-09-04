package org.liara.api.data.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.data.primitive.Primitives;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class StatesByteTable
  extends TableDescription
{
  @NonNull
  public final ColumnDescription stateIdentifier = column(
    "state_identifier", Primitives.INTEGER
  );

  @NonNull
  public final ColumnDescription value = column(
    "value", Primitives.BYTE
  );

  @Override
  public @NonNull String getName () {
    return "states_byte";
  }
}
