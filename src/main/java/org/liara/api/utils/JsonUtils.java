package org.liara.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class JsonUtils
{
  public static @NonNull JsonNode mapToNullNodeIfNull (@Nullable final JsonNode node) {
    return node == null ? NullNode.getInstance() : node;
  }
}
