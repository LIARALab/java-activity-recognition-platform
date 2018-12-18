package org.liara.api.collection;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class InvalidRequestBodyException
  extends Exception
{
  @NonNull
  private final Set<@NonNull ConstraintViolation<@NonNull Object>> _violations;

  public InvalidRequestBodyException (@NonNull final Set<@NonNull ConstraintViolation<@NonNull Object>> violations) {
    _violations = violations;
  }

  public InvalidRequestBodyException (
    @NonNull final String message, @NonNull final Set<@NonNull ConstraintViolation<@NonNull Object>> violations
  )
  {
    super(message);
    _violations = violations;
  }

  public @NonNull Set<@NonNull ConstraintViolation<@NonNull Object>> getViolations () {
    return _violations;
  }
}
