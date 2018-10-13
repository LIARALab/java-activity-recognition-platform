package org.liara.api.data.tree;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public interface NestedSetRepository
{
  <Node extends NestedSet> @NonNull List<@NonNull Node> getChildrenOf (@NonNull final Node node);

  <Node extends NestedSet> @NonNull List<@NonNull Node> getAllChildrenOf (@NonNull final Node node);

  <Node extends NestedSet> @Nullable Node getParentOf (@NonNull final Node node);

  <Node extends NestedSet> @NonNull List<@NonNull Node> getParentsOf (@NonNull final Node node);

  void attachChild (@NonNull final NestedSet node);

  void attachChild (
    @NonNull final NestedSet node, @Nullable final NestedSet parent
  );

  void removeChild (@NonNull final NestedSet node);

  <Node extends NestedSet> Node getRoot (@NonNull final Node node);
}
