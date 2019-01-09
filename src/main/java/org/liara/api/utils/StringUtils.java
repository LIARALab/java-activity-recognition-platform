package org.liara.api.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class StringUtils
{
  @NonNull private final static Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");

  public static @NonNull String toSnakeCase (@NonNull final String camelCase) {
    @NonNull final String result = UPPERCASE_PATTERN.matcher(camelCase).replaceAll(
      (@NonNull final MatchResult match) -> "-" + match.group().toLowerCase()
    );

    return result.startsWith("-") ? result.substring(1) : result;
  }
}
