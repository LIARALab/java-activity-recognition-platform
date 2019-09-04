package org.liara.api.data.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.liara.data.graph.Graph;

import java.util.function.Supplier;

public interface GraphSupplier
  extends Supplier<@NonNull Graph>
{
  @NonNull String getName ();
}
