package org.liara.api.validator;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class OptionalStringRequiredValidator implements ConstraintValidator<Required, Optional<String>>
{
  @Override
  public boolean isValid (
    @Nullable final Optional<String> value,
    @NonNull final ConstraintValidatorContext context
  ) {
    return value.isPresent() && value.get().trim().length() > 0;
  }
}
