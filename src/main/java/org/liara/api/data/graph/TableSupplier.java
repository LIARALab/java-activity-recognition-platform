package org.liara.api.data.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.data.graph.builder.TableBuilder;

public interface TableSupplier
  extends TableBuilder
{
  @NonNull String getName ();
}
