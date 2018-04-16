package org.liara.api.validator;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class OptionalRequiredValidator implements ConstraintValidator<Required, Optional<?>>
{
  @Override
  public boolean isValid (
    @Nullable final Optional<?> value,
    @NonNull final ConstraintValidatorContext context
  ) {
    return value.isPresent();
  }
}
